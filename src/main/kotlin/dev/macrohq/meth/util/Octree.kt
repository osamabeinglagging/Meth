package dev.macrohq.meth.util

import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

class Octree (private val start: Vec3, private val size: Double){
    private val end = start.addVector(size, size, size)
    private var isAirNode: Boolean? = null
    private val points = mutableListOf<BlockPos>()
    private var divided = false
    private var subNodes = mutableListOf<Octree>()

    fun insert(block: BlockPos): Boolean{
        if(!contains(block)) return false
        points.add(block)
        if(!points.any { !world.isAirBlock(it) }){
            subdivide()
            isAirNode = false
        }
        else{
            subNodes.clear()
            isAirNode = true
        }
//        if(!divided){
//            points.add(block)
//            if(world.isAirBlock(block) == isAirNode || isAirNode ==  null){
//                isAirNode = world.isAirBlock(block)
//            }
//            else{
//                subdivide()
//            }
//            return true
//        }
//        for(node in this.subNodes){
//            if(node.insert(block)) return true
//        }
        return false
    }

    private fun contains(block: BlockPos): Boolean{
        return AxisAlignedBB(start.xCoord, start.yCoord, start.zCoord,
            end.xCoord, end.yCoord, end.zCoord).isVecInside(block.toVec3())
    }

     private fun subdivide(){
         divided = true
         val nSize = this.size/2
         subNodes.add(Octree(start, nSize))
         subNodes.add(Octree(start.addVector(nSize, 0.0, 0.0), nSize))
         subNodes.add(Octree(start.addVector(nSize, nSize, 0.0), nSize))
         subNodes.add(Octree(start.addVector(0.0, nSize, 0.0), nSize))

         subNodes.add(Octree(start.addVector(0.0,0.0, nSize), nSize))
         subNodes.add(Octree(start.addVector(nSize, 0.0, nSize), nSize))
         subNodes.add(Octree(start.addVector(nSize, nSize, nSize), nSize))
         subNodes.add(Octree(start.addVector(0.0, nSize, nSize), nSize))

         for(point in points){
             for(node in subNodes){
                 if(node.insert(point)) continue
             }
         }
    }

    fun draw(){
        RenderUtil.aabbs.add(AxisAlignedBB(start.xCoord, start.yCoord, start.zCoord, end.xCoord, end.yCoord, end.zCoord))
        for(node in subNodes){
            node.draw()
        }
    }
}