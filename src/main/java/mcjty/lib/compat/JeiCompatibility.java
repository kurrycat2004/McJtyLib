package mcjty.lib.compat;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.Widget;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

@JEIPlugin
public class JeiCompatibility implements IModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GenericGuiContainer>() {
			@Nonnull
			@Override
			public Class<GenericGuiContainer> getGuiContainerClass() {
				return GenericGuiContainer.class;
			}

			@Nullable
			@Override
			public List<Rectangle> getGuiExtraAreas(GenericGuiContainer guiContainer) {
				GenericGuiContainer<?> container = guiContainer;
				return container.getExtraWindowBounds();
			}

			@Nullable
			@Override
			public Object getIngredientUnderMouse(GenericGuiContainer guiContainer, int mouseX, int mouseY) {
				Widget<?> widget = guiContainer.getWindow().getToplevel().getWidgetAtPosition(mouseX, mouseY);
				if (widget instanceof BlockRender blockRender) {
					return blockRender.getRenderItem();
				}
				return null;
			}
		});

		registry.addGhostIngredientHandler(GenericGuiContainer.class, new JeiGhostIngredientHandler());
	}
}
