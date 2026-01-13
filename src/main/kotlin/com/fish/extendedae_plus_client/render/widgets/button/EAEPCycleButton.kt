package com.fish.extendedae_plus_client.render.widgets.button

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.PacketDistributor
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.IntUnaryOperator

open class EAEPCycleButton(
    protected val states: MutableList<EAEPActionItems>,
    statedOnPress: BiConsumer<Int, EAEPActionItems>,
    protected val stateIterator: IntUnaryOperator
) : EAEPButton(Consumer { button: EAEPButton ->
    if (button is EAEPCycleButton) statedOnPress.accept(button.iterateState(), button.action)
}) {
    protected var stateIndex: Int = 0

    override val action: EAEPActionItems
        get() {
            return this.states[this.stateIndex]
        }

    fun setStateIndex(stateIndex: Int, triggerEvent: Boolean) {
        this.stateIndex = stateIndex
        if (triggerEvent) this.onPress()
        else this.updateTooltip()
    }

    /** @return 被迭代过的 stateIndex
     */
    fun iterateState(): Int {
        this.stateIterator.let { this.stateIndex = it.applyAsInt(this.stateIndex) }
        return this.stateIndex
    }

    class Builder {
        private val states: MutableList<EAEPActionItems> = ArrayList()
        private val tasks: MutableList<Consumer<EAEPActionItems>> = ArrayList()
        private var stateIterator: IntUnaryOperator? = null

        fun addPart(action: EAEPActionItems, packet: CustomPacketPayload): Builder {
            return this.addPart(action, Runnable { PacketDistributor.sendToServer(packet) })
        }

        fun addPart(action: EAEPActionItems, onPress: Runnable): Builder {
            return this.addPart(action) { _ -> onPress.run() }
        }

        fun addPart(action: EAEPActionItems, onPress: Consumer<EAEPActionItems> = Consumer {}): Builder {
            val onPress: Consumer<EAEPActionItems> = onPress

            this.states.add(action)
            this.tasks.add(onPress)
            return this
        }

        fun setIterator(stateIterator: IntUnaryOperator): Builder {
            this.stateIterator = stateIterator
            return this
        }

        fun build(): EAEPCycleButton {
            return EAEPCycleButton(
                this.states,
                { index: Int, action: EAEPActionItems -> this.tasks[index].accept(action) },
                this.stateIterator ?: IntUnaryOperator { index ->
                    (index + 1) % this.states.size
                }
            )
        }
    }
}
