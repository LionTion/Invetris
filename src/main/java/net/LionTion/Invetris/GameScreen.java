package net.LionTion.Invetris;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.NoSuchElementException;

public class GameScreen extends InventoryScreen {
    private Identifier nativeImageID = new Identifier("invetris", "assets/invetris/frame.png");

    public GameScreen(PlayerEntity player) {
        super(player);
        Mod.Open = true;
    }

    public NativeImageBackedTexture updateTexture(BufferedImage frame) throws IOException {
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, 128, 128, false);
        for (int y = 0; y < 128; ++y) {
            for (int x = 0; x < 128; ++x) {
                image.setColor(x, y, frame.getRGB(x, y));
            }
        }
        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
        return texture;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (Mod.Enabled) {
            try {
                TextureManager tm = client.getTextureManager();
                BufferedImage frame = Mod.Tetris.render();
                NativeImageBackedTexture texture = updateTexture(frame);
                this.nativeImageID = tm.registerDynamicTexture("invetris", texture);
                RenderSystem.setShaderTexture(0, texture.getGlId());
                if (getRecipeBookWidget().isOpen()) {
                    drawTexture(matrices, this.width / 2 + 168, this.height / 2 - 48, 0, 0, 128, 128, 128, 128);
                } else {
                    drawTexture(matrices, this.width / 2 + 94, this.height / 2 - 48, 0, 0, 128, 128, 128, 128);
                }

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            } catch (NoSuchElementException | NumberFormatException ignored) {
            }
        }
    }

    @Override
    public void close() {
        super.close();
        Mod.Open = false;
    }

}
