package dev.kidepcode.animatedbans.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

object Mini {
    private val mm: MiniMessage = MiniMessage.builder()
        .tags(StandardTags.defaults())
        .build()

    fun parse(raw: String, resolver: TagResolver = TagResolver.empty()): Component {
        return mm.deserialize(raw, resolver)
    }
}