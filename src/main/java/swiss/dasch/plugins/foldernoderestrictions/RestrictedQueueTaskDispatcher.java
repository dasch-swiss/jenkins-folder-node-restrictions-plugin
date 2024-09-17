package swiss.dasch.plugins.foldernoderestrictions;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.ModelObject;
import hudson.model.Node;
import hudson.model.Queue.BuildableItem;
import hudson.model.Queue.Task;
import hudson.model.queue.CauseOfBlockage;
import hudson.model.queue.QueueTaskDispatcher;

@Extension
public class RestrictedQueueTaskDispatcher extends QueueTaskDispatcher {

	private static final int MAX_DEPTH = 256;

	@Override
	public CauseOfBlockage canTake(Node node, BuildableItem item) {
		return this.canTake(node, item.task);
	}

	@Override
	public CauseOfBlockage canTake(Node node, Task task) {
		Item item = findTaskOwnerItem(task);

		if (item != null && !isItemAllowedOnNode(node, item)) {
			return CauseOfBlockage.fromMessage(Messages._RestrictedQueueTaskDispatcher_TaskBlocked());
		}

		return null;
	}

	private static boolean isItemAllowedOnNode(Node node, Item item) {
		ModelObject current = item.getParent();

		for (int i = 0; i < MAX_DEPTH; ++i) {
			if (current instanceof AbstractFolder) {
				AbstractFolder<?> folder = (AbstractFolder<?>) current;

				NodeRestrictionsFolderProperty property = folder.getProperties()
						.get(NodeRestrictionsFolderProperty.class);

				if (property != null
						&& property.checkRestrictions(node) == NodeRestrictionsFolderProperty.Result.DISALLOWED) {
					return false;
				}
			}

			if (current instanceof Item == false) {
				break;
			} else if (i == MAX_DEPTH - 1) {
				// Couldn't check all parent folders so we must assume the item is not allowed
				// to run
				return false;
			}

			current = ((Item) current).getParent();
		}

		return true;
	}

	private static Item findTaskOwnerItem(Task task) {
		Task current = task;

		for (int i = 0; i < MAX_DEPTH; ++i) {
			if (current instanceof Item) {
				return (Item) current;
			}

			Task owner = current.getOwnerTask();

			if (owner == null || owner == current) {
				break;
			}

			current = owner;
		}

		return null;
	}

}
