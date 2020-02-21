package no.item.xp.plugin.parser

import org.gradle.internal.impldep.com.fasterxml.jackson.databind.JsonNode

interface Parser<T : JsonNode?> : Function<T>
