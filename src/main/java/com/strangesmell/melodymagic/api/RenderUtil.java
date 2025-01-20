package com.strangesmell.melodymagic.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderUtil {

    public static void blit(
            ResourceLocation atlasLocation,
            float x,
            float y,
            float width,
            float height,
            float uOffset,
            float vOffset,
            float uWidth,
            float vHeight,
            float textureWidth,
            float textureHeight,
            GuiGraphics graphics

    ) {
        blit(
                atlasLocation, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight,graphics
        );
    }

    public static void blit(
            ResourceLocation atlasLocation,
            float x1,
            float x2,
            float y1,
            float y2,
            float blitOffset,
            float uWidth,
            float vHeight,
            float uOffset,
            float vOffset,
            float textureWidth,
            float textureHeight,
            GuiGraphics graphics
    ) {
        innerBlit(
                atlasLocation,
                x1,
                x2,
                y1,
                y2,
                blitOffset,
                (uOffset + 0.0F) / textureWidth,
                (uOffset + uWidth) / textureWidth,
                (vOffset + 0.0F) / textureHeight,
                (vOffset + vHeight) / textureHeight,
                graphics
        );
    }

    public static void innerBlit(
            ResourceLocation atlasLocation,
            float x1,
            float x2,
            float y1,
            float y2,
            float blitOffset,
            float minU,
            float maxU,
            float minV,
            float maxV,
            GuiGraphics graphics
    ) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset).setUv(minU, minV);
        bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset).setUv(minU, maxV);
        bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset).setUv(maxU, maxV);
        bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

}
