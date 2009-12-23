package outputdebugstring.ui;

/**
 * Implemented by objects that can somehow be cleared.
 * <p>
 * An Eclipse workbench view could clear its viewer content.
 */
public interface Clearable {
	void clear();
}
