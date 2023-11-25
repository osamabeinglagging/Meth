package dev.macrohq.meth.util

import dev.macrohq.meth.util.RotationUtil.Rotation
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

object AngleUtil {

    fun yawTo360(yaw: Float): Float{
        return (((yaw%360)+360)%360)
    }
    fun getAngles(vec: Vec3): Rotation {
        val diffX = vec.xCoord - player.posX
        val diffY = vec.yCoord - (player.posY + player.getEyeHeight())
        val diffZ = vec.zCoord - player.posZ
        val dist = sqrt(diffX * diffX + diffZ * diffZ)
        val yaw = (atan2(diffZ, diffX) * 180.0 / Math.PI).toFloat() - 90f
        val pitch = (-(atan2(diffY, dist) * 180.0 / Math.PI)).toFloat()
        return Rotation(
            player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw),
            player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch)
        )
    }
    fun getDiffBetweenBlockPos(first: BlockPos, second: BlockPos) = getAngles(first.toVec3()).yaw - getAngles(second.toVec3()).yaw
    fun getAngles(block: BlockPos) = getAngles(block.toVec3())
    fun getAngles(entity: Entity) = getAngles((entity as EntityLivingBase).headPosition())

    fun getNeededChange(startRot: Rotation, endRot: Rotation): Rotation {
        var yawChange = MathHelper.wrapAngleTo180_float(endRot.yaw) - MathHelper.wrapAngleTo180_float(startRot.yaw)
        if (yawChange <= -180.0f) yawChange += 360.0f else if (yawChange > 180.0f) yawChange += -360.0f
        return Rotation(yawChange, endRot.pitch - startRot.pitch)
    }

    fun getYawChange(position: Vec3): Float{
        val startRot = Rotation(player.rotationYaw, 0f)
        val endRot = Rotation(getAngles(position).yaw, 0f)
        return getNeededChange(startRot, endRot).yaw
    }

    fun getPitchChange(position: Vec3): Float = getAngles(position).pitch - player.rotationPitch
    fun angleDifference(block: BlockPos, yawDifference: Float, pitchDifference: Float): Boolean{
        val yawChange = abs(getYawChange(BlockUtil.bestPointOnBlock(block)))
        val pitchChange = abs(getPitchChange(BlockUtil.bestPointOnBlock(block)))
        return yawChange < yawDifference && pitchChange < pitchDifference
    }
    fun angleDifference(position: Vec3, yawDifference: Float, pitchDifference: Float): Boolean{
        return abs(getYawChange(position)) < yawDifference && abs(getPitchChange(position)) < pitchDifference
    }
    fun angleDifference(entity: Entity, yawDifference: Float, pitchDifference: Float): Boolean{
        val entityPos = entity.positionVector.addVector(0.0, 0.8, 0.0)
        return abs(getYawChange(entityPos)) < yawDifference && abs(getPitchChange(entityPos)) < pitchDifference
    }

    fun bowAngle(target: Entity): Rotation{
        // Skidded from Wurst Client I Legit wasted a lot of time watching videos trying to figure out the equation for
        // Projectile Velocity with Linear Drag. cant figure it out :sadge:
        val velocity = 1f
        val d = player.getPositionEyes(1f).distanceTo(target.positionVector.addVector(.5,1.0,.5))
        val posX = (target.posX + (target.posX - target.lastTickPosX) * d - player.posX)
        val posY = (target.posY + (target.posY - target.prevPosY) * d + target.height * 0.5 - player.posY - player.eyeHeight)
        val posZ = (target.posZ + (target.posZ - target.prevPosZ) * d - player.posZ)

        val hDistance = sqrt(posX * posX + posZ * posZ)
        val hDistanceSq = hDistance * hDistance

        val g = 0.006f

        val velocitySq: Float = velocity * velocity
        val velocityPow4 = velocitySq * velocitySq

        val neededYaw = Math.toDegrees(atan2(posZ, posX)).toFloat() - 90
        val neededPitch = -Math.toDegrees(atan((velocitySq - sqrt(velocityPow4 - g * (g * hDistanceSq + 2 * posY * velocitySq))) / (g * hDistance))).toFloat()
        return Rotation(neededYaw, neededPitch)
    }
}
