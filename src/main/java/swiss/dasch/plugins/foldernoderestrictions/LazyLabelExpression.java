package swiss.dasch.plugins.foldernoderestrictions;

import javax.annotation.Nullable;

import antlr.ANTLRException;
import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import jenkins.model.Jenkins;

public class LazyLabelExpression {
	@Nullable
	private final String expression;

	private Label label;
	private boolean invalid;

	public LazyLabelExpression(@Nullable String expression) {
		this.expression = expression;
	}

	@Nullable
	public String getExpression() {
		return this.expression;
	}

	@Nullable
	public Label get() {
		if (this.invalid) {
			return null;
		}

		if (this.label != null) {
			return this.label;
		}

		if (this.expression == null || this.expression.trim().length() == 0) {
			return this.label = new LabelAtom("");
		}

		try {
			// Ensure expression is valid
			Label.parseExpression(this.expression);
			this.label = Jenkins.get().getLabel(this.expression);
		} catch (ANTLRException ex) {
			this.invalid = true;
		}

		return this.label;
	}
}
