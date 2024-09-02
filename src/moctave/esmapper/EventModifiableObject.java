package moctave.esmapper;

public interface EventModifiableObject {
	/**
	 * Applies a node, which is formatted similarly to this one but may contain
	 * flags on its children, as a set of modifiers which change.
	 */
	public void applyModifiers(Node node);
}
