package mcjty.lib.compat;

import com.github.bsideup.jabel.Desugar;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.Widget;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JeiGhostIngredientHandler implements IGhostIngredientHandler<GenericGuiContainer> {
    private static final Point OFFSET = new Point(0, 0);

    @Override
    public <I> List<Target<I>> getTargets(GenericGuiContainer gui, I ingredient, boolean doStart) {
        if (!(ingredient instanceof ItemStack)) return Collections.emptyList();

        Widget<?> topLevelWidget = gui.getWindow().getToplevel();
        List<GhostIngredientTarget> blockRenderList = new ArrayList<>();
        recursiveCollectGhostIngredientTargets(blockRenderList, OFFSET, topLevelWidget);

        return blockRenderList.stream().map(ghostIngredientTarget -> new Target<I>() {
            @Override
            public Rectangle getArea() {
                return ghostIngredientTarget.bounds;
            }

            @Override
            public void accept(I ingredient) {
                ghostIngredientTarget.handler.accept((ItemStack) ingredient);
            }
        }).collect(Collectors.toList());
    }

    private static void recursiveCollectGhostIngredientTargets(List<GhostIngredientTarget> blockRenderList, Point offset, Widget<?> widget) {
        if (widget instanceof BlockRender blockRender) {
            Consumer<ItemStack> ghostIngredientHandler = blockRender.getGhostIngredientHandler();
            if (ghostIngredientHandler == null) return;

            Rectangle bounds = new Rectangle(blockRender.getBounds());
            bounds.translate(offset.x + 1, offset.y + 1);
            bounds.setSize(bounds.width - 2, bounds.height - 2);
            blockRenderList.add(new GhostIngredientTarget(ghostIngredientHandler, bounds));
            return;
        }

        if (!(widget instanceof AbstractContainerWidget<?> container)) return;

        for (Widget<?> child : container.getChildren()) {
            Point containerOffset = new Point(offset);
            containerOffset.translate(container.getBounds().x, container.getBounds().y);
            recursiveCollectGhostIngredientTargets(blockRenderList, containerOffset, child);
        }
    }

    @Override
    public void onComplete() {
    }

    @Desugar
    private record GhostIngredientTarget(Consumer<ItemStack> handler, Rectangle bounds) {}
}
