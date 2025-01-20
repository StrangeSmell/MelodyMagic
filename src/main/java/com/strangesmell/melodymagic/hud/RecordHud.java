package com.strangesmell.melodymagic.hud;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.playerLookControl.CameraLookControl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.lock;
import static com.strangesmell.melodymagic.MelodyMagic.locked_entity;

public class RecordHud implements LayeredDraw.Layer {
    private static final RecordHud hud = new RecordHud();
    public static List<Pair<String, Boolean>> recordSubtitle = new ArrayList<>();
    public static List<Integer> recordSubtitleTime = new ArrayList<>();

    public static RecordHud getInstance() {
        return hud;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        guiGraphics.pose().pushPose();
        for (int i = 0; i < recordSubtitle.size(); ) {
            if (i != 0) {
                if (recordSubtitleTime.get(i) == 1000) {
                    if (recordSubtitleTime.get(i - 1) >= 800) {
                        return;
                    }
                }
            }
            if (recordSubtitle.get(i).getB()) {
                guiGraphics.drawString(Minecraft.getInstance().font, recordSubtitle.get(i).getA(), guiGraphics.guiWidth() - 50 - ((float) Minecraft.getInstance().font.width(recordSubtitle.get(i).getA())) / 2, guiGraphics.guiHeight() - 50 + (float) recordSubtitleTime.get(i) / 20, 16733525, true);
            } else {
                guiGraphics.drawString(Minecraft.getInstance().font, recordSubtitle.get(i).getA(), guiGraphics.guiWidth() - 50 - ((float) Minecraft.getInstance().font.width(recordSubtitle.get(i).getA())) / 2, guiGraphics.guiHeight() - 50 + (float) recordSubtitleTime.get(i) / 20, 16755200, true);

            }
            if (recordSubtitleTime.get(i) <= 0) {
                recordSubtitleTime.remove(i);
                recordSubtitle.remove(i);
            } else {
                recordSubtitleTime.set(i, recordSubtitleTime.get(i) - 1);
                i++;
            }
        }
        guiGraphics.pose().popPose();


        if(locked_entity != null&&!locked_entity.isAlive()){
            lock=false;
            locked_entity =null;
        }
        if(Minecraft.getInstance().level==null)return;

        if(MelodyMagic.lock){
            Player player = Minecraft.getInstance().player;
            CameraLookControl playerLookControl = new CameraLookControl(player ,deltaTracker );
            playerLookControl.setLookAt(locked_entity);
            playerLookControl.tick();

        }
    }
}
