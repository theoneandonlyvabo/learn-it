package com.learnit.app

import com.learnit.app.data.remote.FlashcardParseException
import com.learnit.app.data.remote.GlmResponseParser
import com.learnit.app.data.remote.dto.GlmChoice
import com.learnit.app.data.remote.dto.GlmMessage
import com.learnit.app.data.remote.dto.GlmResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GlmResponseParserTest {
    private val parser = GlmResponseParser()
    private val topic = "Photosynthesis"

    private fun response(content: String) = GlmResponse(
        choices = listOf(GlmChoice(message = GlmMessage(role = "assistant", content = content)))
    )

    @Test
    fun `parses plain JSON array`() {
        val content = """[{"question":"What is ATP?","answer":"Adenosine triphosphate"}]"""
        val result = parser.parse(response(content), topic)
        assertEquals(1, result.size)
        assertEquals("What is ATP?", result[0].question)
        assertEquals("Adenosine triphosphate", result[0].answer)
    }

    @Test
    fun `strips json-labeled fence and parses`() {
        val content = "```json\n[{\"question\":\"Q1\",\"answer\":\"A1\"}]\n```"
        val result = parser.parse(response(content), topic)
        assertEquals(1, result.size)
    }

    @Test
    fun `strips unlabeled code fence and parses`() {
        val content = "```\n[{\"question\":\"Q2\",\"answer\":\"A2\"}]\n```"
        val result = parser.parse(response(content), topic)
        assertEquals(1, result.size)
    }

    @Test
    fun `throws FlashcardParseException on malformed JSON`() {
        assertThrows(FlashcardParseException::class.java) {
            parser.parse(response("not json at all"), topic)
        }
    }

    @Test
    fun `filters out items with blank answer`() {
        val content = """[{"question":"Q1","answer":"A1"},{"question":"Q2","answer":""}]"""
        val result = parser.parse(response(content), topic)
        assertEquals(1, result.size)
        assertEquals("Q1", result[0].question)
    }

    @Test
    fun `throws when all items have blank fields`() {
        assertThrows(FlashcardParseException::class.java) {
            parser.parse(response("""[{"question":"","answer":""}]"""), topic)
        }
    }

    @Test
    fun `sets topic on all parsed cards`() {
        val content = """[{"question":"Q","answer":"A"}]"""
        val result = parser.parse(response(content), topic)
        assertEquals(topic, result[0].topic)
    }
}
