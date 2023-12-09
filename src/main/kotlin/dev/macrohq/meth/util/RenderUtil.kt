package dev.macrohq.meth.util

import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderLiving
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_ALPHA
import java.awt.Color
import kotlin.math.sqrt

object RenderUtil {
  var markers = mutableListOf<BlockPos>()
  var filledBox = mutableListOf<BlockPos>()
  var lines = mutableListOf<Vec3>()
  var entites = mutableListOf<Entity>()
  var points = mutableListOf<Vec3>()
  var aabbs = mutableListOf<AxisAlignedBB>()
  var green = Color(0, 255, 0, 255)
  var white = Color(255, 255, 255)

  fun onRenderWorldLast(event: RenderWorldLastEvent) {
    if(player == null || world == null) return
    val chromaHSB = Color.getHSBColor((System.currentTimeMillis() / 10 % 2000).toFloat() / 2000, 1f, 1f)
    val chroma = Color(chromaHSB.red, chromaHSB.green, chromaHSB.blue, 120)
    for (block in filledBox) {
      drawBox(event, block, green, true)
      drawFilledBox(event, block, chroma, true)
    }
    for (block in markers) {
      drawBox(event, block, green, true)
    }
    for (block in lines) {
      if (lines.indexOf(block) == lines.size - 1) continue
      drawLine(event, block, lines[lines.indexOf(block) + 1], chroma)
    }
    for (point in points) {
      drawPoints(event, point, chroma)
    }
    for (entity in entites) {
      if (entity.isDead) continue
      drawEntity(event, entity, green)
    }
    for (aabb in aabbs) {
      drawBox(event, aabb, white, true)
    }
  }

  fun drawFilledBox(event: RenderWorldLastEvent, blockPos: BlockPos, color: Color, esp: Boolean) {
    val aabb = AxisAlignedBB(
      blockPos.x - 0.01,
      blockPos.y - 0.01,
      blockPos.z - 0.01,
      blockPos.x + 1 + 0.01,
      blockPos.y + 1 + 0.01,
      blockPos.z + 1 + 0.01
    )
    drawFilledBox(event, aabb, color, esp)
  }

  fun drawBox(event: RenderWorldLastEvent, blockPos: BlockPos, color: Color, esp: Boolean) {
    val aabb = AxisAlignedBB(
      blockPos.x.toDouble(),
      blockPos.y.toDouble(),
      blockPos.z.toDouble(),
      (blockPos.x + 1).toDouble(),
      (blockPos.y + 1).toDouble(),
      (blockPos.z + 1).toDouble()
    )
    drawBox(event, aabb, color, esp)
  }

  fun drawPoints(event: RenderWorldLastEvent, point: Vec3, color: Color) {
    val aabb = AxisAlignedBB(
      point.xCoord - 0.07,
      point.yCoord - 0.07,
      point.zCoord - 0.07,
      point.xCoord + 0.07,
      point.yCoord + 0.07,
      point.zCoord + 0.07
    )
    drawBox(event, aabb, color, true)
  }

  fun drawEntity(event: RenderWorldLastEvent, entity: Entity, color: Color) {
    val aabb = AxisAlignedBB(
      entity.posX - .5, entity.posY, entity.posZ - .5, entity.posX + .5, entity.posY + 2, entity.posZ + .5
    )
    drawBox(event, aabb, color, true)
  }

  fun drawBox(event: RenderWorldLastEvent, aabb: AxisAlignedBB, color: Color, esp: Boolean) {
    val tessellator = Tessellator.getInstance()
    val bufferBuilder = tessellator.worldRenderer
    val render = mc.renderViewEntity
    val realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * event.partialTicks
    val realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * event.partialTicks
    val realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * event.partialTicks
    val r = color.red / 255.0f
    val g = color.green / 255.0f
    val b = color.blue / 255.0f
    val a = 255.0f * 0.9f
    GlStateManager.pushMatrix()
    GlStateManager.translate(-realX, -realY, -realZ)
    GlStateManager.disableTexture2D()
    GlStateManager.disableLighting()
    GL11.glDisable(3553)
    GL11.glLineWidth(3f)
    GlStateManager.enableBlend()
    GlStateManager.disableAlpha()
    if (esp) {
      GlStateManager.disableDepth()
    }
    GlStateManager.depthMask(false)
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    //
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    GlStateManager.translate(realX, realY, realZ)
    GlStateManager.disableBlend()
    GlStateManager.enableAlpha()
    GlStateManager.enableTexture2D()
    if (esp) {
      GlStateManager.enableDepth()
    }
    GlStateManager.depthMask(true)
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
  }

  fun drawFilledBox(event: RenderWorldLastEvent, aabb: AxisAlignedBB, color: Color, esp: Boolean) {
    val render = mc.renderViewEntity
    val realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * event.partialTicks
    val realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * event.partialTicks
    val realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * event.partialTicks
    val r = color.red / 255.0f
    val g = color.green / 255.0f
    val b = color.blue / 255.0f
    val a = color.alpha / 255.0f * 0.7f
    GlStateManager.pushMatrix()
    GlStateManager.translate(-realX, -realY, -realZ)
    GlStateManager.disableTexture2D()
    GlStateManager.disableLighting()
    GL11.glDisable(3553)
    GL11.glLineWidth(3f)
    GlStateManager.enableBlend()
    GlStateManager.disableAlpha()
    if (esp) {
      GlStateManager.disableDepth()
    }
    GlStateManager.depthMask(false)
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    val tessellator = Tessellator.getInstance()
    val bufferBuilder = tessellator.worldRenderer
    GlStateManager.color(r, g, b, a)
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex()
    bufferBuilder.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex()
    tessellator.draw()
    GlStateManager.translate(realX, realY, realZ)
    GlStateManager.disableBlend()
    GlStateManager.enableAlpha()
    GlStateManager.enableTexture2D()
    if (esp) {
      GlStateManager.enableDepth()
    }
    GlStateManager.depthMask(true)
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
  }

  fun drawLine(event: RenderWorldLastEvent, blockPos1: Vec3, blockPos2: Vec3, color: Color) {
    drawLine(event.partialTicks, blockPos1, blockPos2, color)
  }

  fun drawLine(partialTicks: Float, blockPos1: Vec3, blockPos2: Vec3, color: Color) {
    val tessellator = Tessellator.getInstance()
    val bufferBuilder = tessellator.worldRenderer
    val render = mc.renderViewEntity
    val realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks
    val realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks
    val realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks
    val r = color.red / 255.0f
    val g = color.green / 255.0f
    val b = color.blue / 255.0f
    val a = 255.0f * 0.9f
    GlStateManager.pushMatrix()
    GlStateManager.translate(-realX, -realY, -realZ)
    GlStateManager.disableTexture2D()
    GlStateManager.disableLighting()
    GL11.glDisable(3553)
    GL11.glLineWidth(3f)
    GlStateManager.disableDepth()
    GlStateManager.disableAlpha()
    GlStateManager.depthMask(false)
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
    bufferBuilder.pos(blockPos1.xCoord, blockPos1.yCoord, blockPos1.zCoord).color(r, g, b, a).endVertex()
    bufferBuilder.pos(blockPos2.xCoord, blockPos2.yCoord, blockPos2.zCoord).color(r, g, b, a).endVertex()
    tessellator.draw()
    GlStateManager.translate(realX, realY, realZ)
    GlStateManager.disableBlend()
    GlStateManager.enableAlpha()
    GlStateManager.enableTexture2D()
    //        GlStateManager.enableDepth();
    GlStateManager.depthMask(true)
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    GlStateManager.popMatrix()
  }

  fun renderText(pos: Vec3, text: String?) {
    val renderPosX: Double = mc.renderManager.viewerPosX
    val renderPosY: Double = mc.renderManager.viewerPosY
    val renderPosZ: Double = mc.renderManager.viewerPosZ
    val x = pos.xCoord - renderPosX
    val y = pos.yCoord - renderPosY
    val z = pos.zCoord - renderPosZ
    GlStateManager.pushMatrix()
    GlStateManager.translate(x + 0.5, y + 1.2, z + 0.5)
    GlStateManager.rotate(-mc.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
    GlStateManager.rotate(mc.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
    val scale = 0.003 * sqrt(player.getDistanceSq(pos.toBlockPos()))
    GlStateManager.scale(-scale, -scale, scale)
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GlStateManager.disableLighting()
    GlStateManager.depthMask(false)
    GlStateManager.disableDepth()
    GlStateManager.enableBlend()
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    mc.fontRendererObj.drawString(text, 0, 0, 0xFFFFFF)
    GlStateManager.enableDepth()
    GlStateManager.depthMask(true)
    GlStateManager.enableLighting()
    GlStateManager.disableBlend()
    GlStateManager.popMatrix()
  }
}