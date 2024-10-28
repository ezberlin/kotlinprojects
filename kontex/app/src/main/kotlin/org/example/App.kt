package org.example

sealed class LatexElement {
    data class Document(val elements: List<LatexElement>) : LatexElement()
    data class Section(val title: String, val elements: List<LatexElement>) : LatexElement()
    data class Paragraph(val text: String) : LatexElement()
    data class Math(val expression: String) : LatexElement()
    data class Equation(val expression: String) : LatexElement()
    data class Figure(val caption: String, val source: String) : LatexElement()
}

class LatexDsl {
    private val elements = mutableListOf<LatexElement>()

    fun document(block: LatexDsl.() -> Unit) {
        elements.add(LatexElement.Document(elements.toList()))
        block()
    }

    fun section(title: String, block: LatexDsl.() -> Unit) {
        val sectionElements = mutableListOf<LatexElement>()
        val sectionDsl = LatexDsl()
        sectionDsl.block()
        elements.add(LatexElement.Section(title, sectionElements + sectionDsl.elements))
    }

    fun paragraph(text: String) {
        elements.add(LatexElement.Paragraph(text))
    }

    fun math(expression: String) {
        elements.add(LatexElement.Math(expression))
    }

    fun equation(expression: String) {
        elements.add(LatexElement.Equation(expression))
    }

    fun figure(caption: String, source: String) {
        elements.add(LatexElement.Figure(caption, source))
    }

    fun generate(): String {
        val builder = StringBuilder()
        builder.appendLine("\\documentclass{article}")
        builder.appendLine("\\begin{document}")
        generateElements(elements, builder)
        builder.appendLine("\\end{document}")
        return builder.toString()
    }

    private fun generateElements(elements: List<LatexElement>, builder: StringBuilder) {
        for (element in elements) {
            when (element) {
                is LatexElement.Document -> generateElements(element.elements, builder)
                is LatexElement.Section -> {
                    builder.appendLine("\\section{${element.title}}")
                    generateElements(element.elements, builder)
                }
                is LatexElement.Paragraph -> builder.appendLine("\\paragraph{${element.text}}")
                
                is LatexElement.Math -> builder.appendLine("${element.expression}$")

                is LatexElement.Equation -> {
                    builder.appendLine("\\begin{equation}")
                    builder.appendLine(element.expression)
                    builder.appendLine("\\end{equation}")}

                is LatexElement.Figure -> {
                    builder.appendLine("\\begin{figure}")
                    builder.appendLine("\\caption{${element.caption}}")
                    builder.appendLine("\\includegraphics{${element.source}}")
                    builder.appendLine("\\end{figure}")
                }
            }
        }
    }
}

fun main() {
    val latex = LatexDsl()
    latex.document {
        section("Introduction") {
            paragraph("This is the introduction.")
            math("x + y = z")
        }
        section("Body") {
            paragraph("This is the body.")
            equation("a^2 + b^2 = c^2")
            figure("My Figure", "figure.png")
        }
    }
    println(latex.generate())
}
