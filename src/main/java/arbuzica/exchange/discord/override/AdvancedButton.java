package arbuzica.exchange.discord.override;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedButton {
    //fields
    private ButtonStyle style;
    private String id;
    private String label;
    private String url;
    private String[] values;
    private boolean disabled;

    //builders
    public static AdvancedButton builder() {
        return new AdvancedButton();
    }

    public static AdvancedButton primary() {
        return builder().style(ButtonStyle.PRIMARY);
    }

    public static AdvancedButton success() {
        return builder().style(ButtonStyle.SUCCESS);
    }

    public static AdvancedButton danger() {
        return builder().style(ButtonStyle.DANGER);
    }

    public static AdvancedButton secondary() {
        return builder().style(ButtonStyle.SECONDARY);
    }

    public static AdvancedButton link() {
        return builder().style(ButtonStyle.LINK);
    }

    //setters
    public AdvancedButton style(ButtonStyle style) {
        this.style = style;
        return this;
    }

    public AdvancedButton id(String id) {
        this.id = id;
        return this;
    }

    public AdvancedButton label(String label) {
        this.label = label;
        return this;
    }

    public AdvancedButton url(String url) {
        this.url = url;
        return this;
    }

    public AdvancedButton values(String... values) {
        this.values = values;
        return this;
    }

    public AdvancedButton disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    //build
    public Button build() {
        if (style == ButtonStyle.LINK) {
            return Button.link(url, label).withDisabled(disabled);
        } else {
            return Button.of(style, valuesButtonId(), label).withDisabled(disabled);
        }
    }

    //utilities
    private String valuesButtonId() {
        if (values == null) {
            return id;
        }

        StringBuilder buttonId = new StringBuilder(id);

        for (String value : values) {
            buttonId.append("/").append(value);
        }

        return buttonId.toString();
    }
}
