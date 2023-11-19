package dev.macrohq.meth.util

import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun EntityLivingBase.getStandingOnCeil() = BlockPos(posX, ceil(posY) - 1, posZ)
fun EntityLivingBase.getStandingOnFloor() = BlockPos(posX, floor(posY) -1, posZ)
fun EntityLivingBase.lastTickPositionCeil() = BlockPos(lastTickPosX, ceil(lastTickPosY) - 1, lastTickPosZ)
fun EntityLivingBase.distanceToBlock(block: BlockPos) = sqrt(getDistanceSqToCenter(block))
fun EntityLivingBase.headPosition(): Vec3 = positionVector.addVector(0.0, eyeHeight.toDouble(), 0.0)
fun BlockPos.toVec3() = Vec3(x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5)
fun BlockPos.toVec3Top(): Vec3 = toVec3().addVector(0.0,0.5,0.0)
fun Vec3.toBlockPos(): BlockPos = BlockPos(xCoord, yCoord, zCoord)
fun Vec3.multiply(multiplyBy: Double): Vec3 = Vec3(xCoord*multiplyBy, yCoord*multiplyBy, zCoord*multiplyBy)
fun KeyBinding.setPressed(pressed: Boolean) = KeyBinding.setKeyBindState(keyCode, pressed)