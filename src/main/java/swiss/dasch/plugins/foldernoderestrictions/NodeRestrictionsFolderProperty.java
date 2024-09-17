package swiss.dasch.plugins.foldernoderestrictions;

import javax.annotation.Nullable;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;

import hudson.Extension;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Label;
import hudson.model.Node;
import hudson.model.labels.LabelExpression;
import hudson.util.FormValidation;

public class NodeRestrictionsFolderProperty extends AbstractFolderProperty<AbstractFolder<?>> {

	private boolean enableAllowedLabels;

	@Nullable
	private String allowedLabelExpression;

	@Nullable
	private transient LazyLabelExpression allowedLabel;

	private boolean enableDisallowedLabels;

	@Nullable
	private String disallowedLabelExpression;

	@Nullable
	private transient LazyLabelExpression disallowedLabel;

	@DataBoundConstructor
	public NodeRestrictionsFolderProperty() {
	}

	@DataBoundSetter
	public synchronized void setEnableAllowedLabels(boolean enabled) {
		this.enableAllowedLabels = enabled;
	}

	public boolean getEnableAllowedLabels() {
		return this.enableAllowedLabels;
	}

	@DataBoundSetter
	public synchronized void setAllowedLabels(String labels) {
		if (labels != null && labels.trim().length() > 0) {
			this.allowedLabelExpression = labels;
		} else {
			this.allowedLabelExpression = null;
		}
		this.allowedLabel = new LazyLabelExpression(this.allowedLabelExpression);
	}

	public String getAllowedLabels() {
		return this.allowedLabelExpression;
	}

	@DataBoundSetter
	public synchronized void setEnableDisallowedLabels(boolean enabled) {
		this.enableDisallowedLabels = enabled;
	}

	public boolean getEnableDisallowedLabels() {
		return this.enableDisallowedLabels;
	}

	@DataBoundSetter
	public synchronized void setDisallowedLabels(String labels) {
		if (labels != null && labels.trim().length() > 0) {
			this.disallowedLabelExpression = labels;
		} else {
			this.disallowedLabelExpression = null;
		}
		this.disallowedLabel = new LazyLabelExpression(this.disallowedLabelExpression);
	}

	public String getDisallowedLabels() {
		return this.disallowedLabelExpression;
	}

	public static enum Result {
		DISABLED, UNSPECIFIED, ALLOWED, DISALLOWED
	}

	public synchronized Result checkRestrictions(Node node) {
		if (!this.enableAllowedLabels && !this.enableDisallowedLabels) {
			return Result.DISABLED;
		}

		if (this.enableDisallowedLabels) {
			if (this.disallowedLabel == null) {
				this.disallowedLabel = new LazyLabelExpression(this.disallowedLabelExpression);
			}

			Label label = this.disallowedLabel.get();

			if (label == null || label.matches(node)) {
				return Result.DISALLOWED;
			}
		}

		if (this.enableAllowedLabels) {
			if (this.allowedLabel == null) {
				this.allowedLabel = new LazyLabelExpression(this.allowedLabelExpression);
			}

			Label label = this.allowedLabel.get();

			if (label != null && label.matches(node)) {
				return Result.ALLOWED;
			}

			return Result.DISALLOWED;
		}

		return Result.UNSPECIFIED;
	}

	@Extension
	@Symbol("nodeRestrictions")
	public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

		@POST
		public FormValidation doCheckAllowedLabels(@AncestorInPath AbstractFolder<?> folder,
				@QueryParameter String value) {
			return LabelExpression.validate(value, folder);
		}

		public AutoCompletionCandidates doAutoCompleteAllowedLabels(@QueryParameter String value) {
			return LabelExpression.autoComplete(value);
		}

		@POST
		public FormValidation doCheckDisallowedLabels(@AncestorInPath AbstractFolder<?> folder,
				@QueryParameter String value) {
			return LabelExpression.validate(value, folder);
		}

		public AutoCompletionCandidates doAutoCompleteDisallowedLabels(@QueryParameter String value) {
			return LabelExpression.autoComplete(value);
		}

	}

}
