package thunder.hack.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import thunder.hack.events.impl.EventPlayerJump;
import thunder.hack.events.impl.EventPlayerTravel;
import thunder.hack.events.impl.Render3DEvent;
import thunder.hack.modules.Module;
import thunder.hack.modules.render.HoleEsp;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.player.PlaceUtility;
import thunder.hack.utility.player.PlayerUtil;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HoleSnap extends Module {
    public HoleSnap() {
        super("HoleSnap", Category.MOVEMENT);
    }
    
    private BlockPos hole;
    private float prevClientYaw;

    @Override
    public void onEnable(){
        hole = findHole();
    }
    
    @Subscribe
    public void modifyVelocity(EventPlayerTravel e){
        if(mc.player.age % 10 == 0) hole = findHole();
        if(hole != null){
            if(e.isPre()){
                prevClientYaw = mc.player.getYaw();
                mc.player.setYaw(PlaceUtility.calculateAngle(hole.toCenterPos())[0]);
            } else {
                mc.player.setYaw(prevClientYaw);
            }
        }
    }

    @Subscribe
    public void modifyJump(EventPlayerJump e){
        if(hole != null){
            if(e.isPre()){
                prevClientYaw = mc.player.getYaw();
                mc.player.setYaw(PlaceUtility.calculateAngle(hole.toCenterPos())[0]);
            } else {
                mc.player.setYaw(prevClientYaw);
            }
        }
    }

    private BlockPos findHole() {
        ArrayList<BlockPos> bloks = new ArrayList<>();
        BlockPos centerPos = mc.player.getBlockPos();
        for (int i = centerPos.getX() - 3; i < centerPos.getX() + 3; i++) {
            for (int j = centerPos.getY() - 4; j < centerPos.getY() + 2; j++) {
                for (int k = centerPos.getZ() - 3; k < centerPos.getZ() + 3; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (validObi(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validBedrock(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validTwoBlockBedrockXZ(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validTwoBlockObiXZ(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validTwoBlockBedrockXZ1(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validTwoBlockObiXZ1(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validQuadBedrock(pos)) {
                        bloks.add(new BlockPos(pos));
                    } else
                    if (validQuadObby(pos)) {
                        bloks.add(new BlockPos(pos));
                    }
                }
            }
        }

        float nearestDistance = 10;
        BlockPos fbp = null;
        for(BlockPos bp : bloks){
            if(BlockPos.ofFloored(mc.player.getPos()).equals(bp)) {
                disable();
                return null;
            }
            if(mc.player.squaredDistanceTo(bp.toCenterPos()) < nearestDistance){
                nearestDistance = (float) mc.player.squaredDistanceTo(bp.toCenterPos());
                fbp = bp;
            }
        }
        return fbp;
    }

    public static boolean validObi(BlockPos pos) {
        return !validBedrock(pos)
                && (isObby(pos.add(0, -1, 0)) || isBedrock(pos.add(0, -1, 0)))
                && (isObby(pos.add(1, 0, 0)) || isBedrock(pos.add(1, 0, 0)))
                && (isObby(pos.add(-1, 0, 0)) || isBedrock(pos.add(-1, 0, 0)))
                && (isObby(pos.add(0, 0, 1)) || isBedrock(pos.add(0, 0, 1)))
                && (isObby(pos.add(0, 0, -1)) || isBedrock(pos.add(0, 0, -1)))
                && isAir(pos)
                && isAir(pos.add(0, 1, 0))
                && isAir(pos.add(0, 2, 0));
    }

    public static  boolean validBedrock(BlockPos pos) {
        return isBedrock(pos.add(0, -1, 0))
                && isBedrock(pos.add(1, 0, 0))
                && isBedrock(pos.add(-1, 0, 0))
                && isBedrock(pos.add(0, 0, 1))
                && isBedrock(pos.add(0, 0, -1))
                && isAir(pos)
                && isAir(pos.add(0, 1, 0))
                && isAir(pos.add(0, 2, 0));
    }

    public static boolean validTwoBlockObiXZ(BlockPos pos) {
        if (
                (isObby(pos.down()) ||  isBedrock(pos.down()))
                        && (isObby(pos.west()) || isBedrock(pos.west()))
                        && (isObby(pos.south()) || isBedrock(pos.south()))
                        && (isObby(pos.north()) || isBedrock(pos.north()))
                        && isAir(pos)
                        && isAir(pos.up())
                        && isAir(pos.up(2))
                        && (isObby(pos.east().down()) || isBedrock(pos.east().down()))
                        && (isObby(pos.east(2)) || isBedrock(pos.east(2)))
                        && (isObby(pos.east().south()) || isBedrock(pos.east().south()))
                        && (isObby(pos.east().north()) || isBedrock(pos.east().north()))
                        && isAir(pos.east())
                        && isAir(pos.east().up())
                        && isAir(pos.east().up(2))
        ) {
            return true;
        }
        return false;
    }

    public static boolean validTwoBlockObiXZ1(BlockPos pos) {
        if (
                (isObby(pos.down()) || isBedrock(pos.down()))
                        && (isObby(pos.west()) || isBedrock(pos.west()))
                        && (isObby(pos.east()) || isBedrock(pos.east()))
                        && (isObby(pos.north()) || isBedrock(pos.north()))
                        && isAir(pos)
                        && isAir(pos.up())
                        && isAir(pos.up(2))
                        && (isObby(pos.south().down()) || isBedrock(pos.south().down()))
                        && (isObby(pos.south(2)) || isBedrock(pos.south(2)))
                        && (isObby(pos.south().east()) || isBedrock(pos.south().east()))
                        && (isObby(pos.south().west()) || isBedrock(pos.south().west()))
                        && isAir(pos.south())
                        && isAir(pos.south().up())
                        && isAir(pos.south().up(2))
        ) {
            return true;
        }
        return false;
    }

    public static boolean validQuadObby(BlockPos pos){
        if(
                ((isObby(pos.down()) || isBedrock(pos.down())) && (isAir(pos)) && isAir(pos.up()) && isAir(pos.up(2)))
                        && ((isObby(pos.south().down()) || isBedrock(pos.south().down())) && (isAir(pos.south())) && isAir(pos.south().up()) && isAir(pos.south().up(2)))
                        && ((isObby(pos.east().down()) || isBedrock(pos.east().down())) && (isAir(pos.east())) && isAir(pos.east().up()) && isAir(pos.east().up(2)))
                        && ((isObby(pos.south().east().down()) || isBedrock(pos.south().east().down())) && (isAir(pos.south().east())) && isAir(pos.south().east().up()) && isAir(pos.south().east().up(2)))

                        &&((isObby(pos.north()) || isBedrock(pos.north())) && (isObby(pos.west()) || isBedrock(pos.west())))
                        &&((isObby(pos.east().north()) || isBedrock(pos.east().north())) && (isObby(pos.east().east()) || isBedrock(pos.east().east())))
                        &&((isObby(pos.south().west()) || isBedrock(pos.south().west())) && (isObby(pos.south().south()) || isBedrock(pos.south().south())))
                        &&((isObby(pos.east().south().south()) || isBedrock(pos.east().south().south())) && (isObby(pos.east().south().east()) || isBedrock(pos.east().south().east())))
        ) return true;

        return false;
    }

    public static boolean validQuadBedrock(BlockPos pos){
        if(
                ((isBedrock(pos.down())) && (isAir(pos)) && isAir(pos.up()) && isAir(pos.up(2)))
                        && ((isBedrock(pos.south().down())) && (isAir(pos.south())) && isAir(pos.south().up()) && isAir(pos.south().up(2)))
                        && ((isBedrock(pos.east().down())) && (isAir(pos.east())) && isAir(pos.east().up()) && isAir(pos.east().up(2)))
                        && ((isBedrock(pos.south().east().down())) && (isAir(pos.south().east())) && isAir(pos.south().east().up()) && isAir(pos.south().east().up(2)))

                        &&(isBedrock(pos.north()) && isBedrock(pos.west()))
                        &&(isBedrock(pos.east().north()) && isBedrock(pos.east().east()))
                        &&(isBedrock(pos.south().west()) && isBedrock(pos.south().south()))
                        &&(isBedrock(pos.east().south().south()) && isBedrock(pos.east().south().east()))

        ) return true;

        return false;
    }

    public static boolean validTwoBlockBedrockXZ(BlockPos pos) {
        if (
                (isBedrock(pos.down()))
                        && (isBedrock(pos.west()))
                        && (isBedrock(pos.south()))
                        && (isBedrock(pos.north()))
                        && isAir(pos)
                        && isAir(pos.up())
                        && isAir(pos.up(2))
                        && (isBedrock(pos.east().down()))
                        && (isBedrock(pos.east(2)))
                        && (isBedrock(pos.east().south()))
                        && (isBedrock(pos.east().north()))
                        && isAir(pos.east())
                        && isAir(pos.east().up())
                        && isAir(pos.east().up(2))
        ) {
            return true;
        }
        return false;
    }

    public static boolean validTwoBlockBedrockXZ1(BlockPos pos) {
        if (
                (isBedrock(pos.down()))
                        && (isBedrock(pos.west()))
                        && (isBedrock(pos.east()))
                        && (isBedrock(pos.north()))
                        && isAir(pos)
                        && isAir(pos.up())
                        && isAir(pos.up(2))
                        && (isBedrock(pos.south().down()))
                        && (isBedrock(pos.south(2)))
                        && (isBedrock(pos.south().east()))
                        && (isBedrock(pos.south().west()))
                        && isAir(pos.south())
                        && isAir(pos.south().up())
                        && isAir(pos.south().up(2))
        ) {
            return true;
        }
        return false;
    }

    private static boolean isObby(BlockPos bp){
        return mc.world.getBlockState(bp).getBlock() == Blocks.OBSIDIAN;
    }

    private static boolean isBedrock(BlockPos bp){
        return mc.world.getBlockState(bp).getBlock() == Blocks.BEDROCK;
    }

    private static boolean isAir(BlockPos bp){
        return mc.world.getBlockState(bp).getBlock() == Blocks.AIR;
    }
}
