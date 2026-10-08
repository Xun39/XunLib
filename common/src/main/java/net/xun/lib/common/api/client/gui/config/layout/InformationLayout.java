package net.xun.lib.common.api.client.gui.config.layout;

/**
 * Information panel sections.
 * <p>
 * The panel consists of three logical sections:
 * <p>
 *     title
 *     description
 *     type
 * <p>
 * The actual widgets are laid out with LinearLayout.
 */
public record InformationLayout(
        int paddingLeft,
        int paddingRight,
        int paddingTop,

        int titleHeight,
        int titleDescriptionGap,

        int descriptionHeight,
        int descriptionTypeGap,

        int typeSeparatorHeight,
        int typeLabelOffsetY,
        int typeValueOffsetY
) {
    public static final InformationLayout DEFAULT =
            new InformationLayout(
                    10,
                    10,
                    12,

                    9,
                    13,

                    30,
                    0,

                    1,
                    11,
                    25
            );

    public int contentWidth(int panelWidth) {
        return Math.max(0, panelWidth - paddingLeft - paddingRight);
    }

    public int descriptionTop() {
        return paddingTop + titleHeight + titleDescriptionGap;
    }

    public int typeTop() {
        return descriptionTop() + descriptionHeight + descriptionTypeGap;
    }

    public int typeLabelTop() {
        return typeTop() + typeLabelOffsetY;
    }

    public int typeValueTop() {
        return typeTop() + typeValueOffsetY;
    }
}