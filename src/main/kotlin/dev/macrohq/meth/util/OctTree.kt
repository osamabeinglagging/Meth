package dev.macrohq.meth.util

import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos

class OctTree(x: Double, y: Double, z: Double, val size: Double) {
    private val boundingBox = BB(x,y,z,x+size, y+size, z+size)
    private var children = mutableSetOf<Leaf>()
    private var subNodes = mutableListOf<OctTree>()
    constructor(bb: BB, size: Double): this(bb.minX, bb.minY, bb.minZ, size)

    fun insert(leaf: Leaf) {
        if (!boundingBox.contains(leaf)) return
        children.removeIf{ it == leaf }
        children.add(leaf)

        if(subNodes.isNotEmpty()){
            for(node in subNodes){
                node.insert(leaf)
            }
        }
        else{
            subdivide()
        }

        if(children.size != (size * size * size).toInt()) return

        if (children.all{ it.isAir == leaf.isAir }){
            subNodes.clear()
        }
    }

    private fun subdivide() {
        val nSize = this.size / 2.0
        if(nSize < 1.0) return

        subNodes.add(OctTree(boundingBox, nSize))
        subNodes.add(OctTree(boundingBox.add(nSize, 0.0, 0.0), nSize))
        subNodes.add(OctTree(boundingBox.add(nSize, nSize, 0.0), nSize))
        subNodes.add(OctTree(boundingBox.add(0.0, nSize, 0.0), nSize))

        subNodes.add(OctTree(boundingBox.add(0.0, 0.0, nSize), nSize))
        subNodes.add(OctTree(boundingBox.add(nSize, 0.0, nSize), nSize))
        subNodes.add(OctTree(boundingBox.add(nSize, nSize, nSize), nSize))
        subNodes.add(OctTree(boundingBox.add(0.0, nSize, nSize), nSize))

        for (child in children) {
            for (node in subNodes) {
                node.insert(child)
            }
        }
    }
    fun draw(){
        RenderUtil.aabbs.add(AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ,
            boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ))
        for(node in subNodes){
            node.draw()
        }
    }
}

class Leaf(val x: Int, val y: Int, val z: Int, val isAir: Boolean){
    constructor(block: BlockPos, isAir: Boolean): this(block.x, block.y, block.z, isAir)

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Leaf) return false
        return this.x == other.x && this.y == other.y && this.z == other.z
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + isAir.hashCode()
        return result
    }
}

class BB(val minX: Double, val minY: Double, val minZ: Double, val maxX: Double, val maxY: Double, val maxZ: Double){
    constructor(leaf: Leaf): this(leaf.x.toDouble(), leaf.y.toDouble(), leaf.z.toDouble(),
        (leaf.x+1).toDouble(), (leaf.y+1).toDouble(), (leaf.z+1).toDouble()
    )

    fun contains(leaf: Leaf): Boolean{
        return if (leaf.x+0.5 > this.minX && leaf.x+0.5 < this.maxX) {
            if (leaf.y+0.5 > this.minY && leaf.y+0.5 < this.maxY) {
                leaf.z+0.5 > this.minZ && leaf.z+0.5 < this.maxZ;
            } else {
                false;
            }
        } else {
            false;
        }
    }

    fun add(x: Double, y: Double, z: Double): BB{
        return BB(minX+x, minY+y, minZ+z, maxX+x, maxY+y, maxZ+z)
    }
}