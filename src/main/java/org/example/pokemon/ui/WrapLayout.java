package org.example.pokemon.ui;

import java.awt.*;
import java.util.Objects;

/**
 * A custom layout manager that wraps components to the next line when they don't fit horizontally.
 * Similar to FlowLayout but properly respects the container's width for wrapping.
 */
public class WrapLayout extends FlowLayout {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a WrapLayout with centered alignment and default gaps.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Creates a WrapLayout with specified alignment and default gaps.
     *
     * @param align the alignment value (LEFT, CENTER, RIGHT, LEADING, TRAILING)
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Creates a WrapLayout with specified alignment and gaps.
     *
     * @param align the alignment value
     * @param hgap  the horizontal gap between components
     * @param vgap  the vertical gap between components
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Returns the preferred dimensions for this layout given the visible components in the container.
     * This is overridden to properly calculate height based on wrapping behavior.
     *
     * @param target the container that needs to be laid out
     * @return the preferred dimensions to lay out the subcomponents of the specified container
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Returns the minimum dimensions needed to layout the visible components contained in the container.
     *
     * @param target the container that needs to be laid out
     * @return the minimum dimensions to lay out the subcomponents of the specified container
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Calculates the layout size based on whether we want preferred or minimum size.
     *
     * @param target    the container to calculate size for
     * @param preferred true for preferred size, false for minimum size
     * @return the calculated dimension
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            Container container = target;

            // When the frame is being resized, targetWidth might be 0
            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }
            targetWidth = container.getSize().width;

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            // Fit components into the width
            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    // Can't add the component to current row. Start a new row.
                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    // Add a horizontal gap for all components after the first
                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            // When using a scroll pane or the DecoratedLookAndFeel we need to
            // make sure the preferred size is less than the size of the target
            // container so shrinking the container size works correctly.
            Container scrollPane = javax.swing.SwingUtilities.getAncestorOfClass(
                    javax.swing.JScrollPane.class, target);

            if (scrollPane != null && target.isValid()) {
                dim.width -= (hgap + 1);
            }

            return dim;
        }
    }

    /**
     * Adds a row's dimensions to the overall dimension.
     *
     * @param dim       the overall dimension to add to
     * @param rowWidth  the width of the current row
     * @param rowHeight the height of the current row
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}
