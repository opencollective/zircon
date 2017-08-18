package org.codetome.zircon.graphics.impl

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.Position
import org.codetome.zircon.Position.Companion.DEFAULT_POSITION
import org.codetome.zircon.Position.Companion.OFFSET_1x1
import org.codetome.zircon.TextCharacter
import org.codetome.zircon.api.TextCharacterBuilder
import org.codetome.zircon.graphics.image.DefaultTextImage
import org.codetome.zircon.Size
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class DefaultTextImageTest {

    lateinit var target: DefaultTextImage

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        target = DefaultTextImage(
                size = SIZE,
                toCopy = TO_COPY,
                filler = FILLER)
    }

    @Test
    fun shouldContainToCopyWhenCreated() {
        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(TO_COPY_CHAR)
    }

    @Test
    fun shouldContainFillerWhenCreated() {
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(FILLER)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(FILLER)
        assertThat(target.getCharacterAt(Position(2, 2)).get())
                .isEqualTo(FILLER)
    }

    @Test
    fun shouldReportProperSizeWhenGetSizeIsCalled() {
        assertThat(target.getBoundableSize()).isEqualTo(SIZE)
    }

    @Test
    fun shouldProperlyResizeWhenResizeCalledWithDifferentSize() {
        val result = target.resize(Size(4, 4), SET_ALL_CHAR)
        assertThat(result.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(TO_COPY_CHAR)
        assertThat(result.getCharacterAt(Position(1, 1)).get())
                .isEqualTo(FILLER)
        assertThat(result.getCharacterAt(Position(3, 3)).get())
                .isEqualTo(SET_ALL_CHAR)
    }

    @Test
    fun shouldNotResizeWhenResizeIsCalledWithSameSize() {
        val result = target.resize(target.getBoundableSize(), TO_COPY_CHAR)

        assertThat(result).isSameAs(target)
    }

    @Test
    fun shouldNotSetAnythingWhenSetCharAtIsCalledWithOutOfBounds() {
        fetchOutOfBoundsPositions().forEach {

            target.setCharacterAt(it, SET_ALL_CHAR)
            assertThat(fetchTargetChars().filter { it == SET_ALL_CHAR }).isEmpty()
        }
    }

    @Test
    fun shouldSetCharProperlyWhenCalledWithinBounds() {
        target.setCharacterAt(OFFSET_1x1, SET_ALL_CHAR)
        assertThat(target.getCharacterAt(OFFSET_1x1).get())
                .isEqualTo(SET_ALL_CHAR)
    }

    @Test
    fun shouldThrowExceptionWhenGettingCharOutOfBounds() {
        fetchOutOfBoundsPositions().forEach {
            var ex: Exception? = null
            try {
                target.getCharacterAt(it).get()
            } catch (e: Exception) {
                ex = e
            }
            assertThat(ex).isNotNull()
        }
    }

    @Test
    fun shouldProperlyCopyWithBasicParams() {
        IMAGE_TO_COPY.drawOnto(target)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
    }

    @Test
    fun shouldProperlyCopyWhenOffsettingRow() {
        IMAGE_TO_COPY.drawOnto(
                destination = target,
                destinationRowOffset = 1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(SET_ALL_CHAR)
    }

    @Test
    fun shouldProperlyCopyWhenOffsettingColumn() {
        IMAGE_TO_COPY.drawOnto(
                destination = target,
                destinationColumnOffset = 1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(SET_ALL_CHAR)
    }

    @Test
    fun shouldProperlyCopyWhenStartAtSecondRow() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                startRowIndex = 1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(FILLER)

    }

    @Test
    fun shouldProperlyCopyWhenStartAtSecondColumn() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                startColumnIndex = 1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(FILLER)

    }

    @Test
    fun shouldProperlyCopyWhenStartAtMinusOneColumn() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                startColumnIndex = -1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(TO_COPY_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(Position(1, 1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(2)).get())
                .isEqualTo(FILLER)
    }

    @Test
    fun shouldProperlyCopyWhenStartAtMinusOneRow() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                startRowIndex = -1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(TO_COPY_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(Position(1, 1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(2)).get())
                .isEqualTo(FILLER)
    }

    @Test
    fun shouldProperlyCopyWhenRowOffsetIsNegative() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                destinationRowOffset = -1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(FILLER)
    }

    @Test
    fun shouldProperlyCopyWhenColumnOffsetIsNegative() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                destinationColumnOffset = -1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(FILLER)
    }

    @Test
    fun shouldProperlyCopyWhenOneRowToCopy() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                rows = 1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(FILLER)

    }

    @Test
    fun shouldProperlyCopyWhenOneColToCopy() {
        IMAGE_TO_COPY_AND_CROP.drawOnto(
                destination = target,
                columns = 1)

        assertThat(target.getCharacterAt(DEFAULT_POSITION).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeRow(1)).get())
                .isEqualTo(SET_ALL_CHAR)
        assertThat(target.getCharacterAt(DEFAULT_POSITION.withRelativeColumn(1)).get())
                .isEqualTo(FILLER)

    }

    private fun fetchTargetChars(): List<TextCharacter> {
        return (0..2).flatMap { col ->
            (0..2).map { row ->
                target.getCharacterAt(Position(col, row)).get()
            }
        }
    }

    private fun fetchOutOfBoundsPositions(): List<Position> {
        return listOf(Position(Int.MAX_VALUE, Int.MAX_VALUE),
                Position(Int.MIN_VALUE, Int.MAX_VALUE),
                Position(Int.MAX_VALUE, Int.MIN_VALUE),
                Position(Int.MIN_VALUE, Int.MIN_VALUE))
    }

    companion object {
        val SIZE = Size(3, 3)
        val FILLER = TextCharacterBuilder.newBuilder()
                .character('a')
                .build()
        val TO_COPY_CHAR = TextCharacterBuilder.newBuilder()
                .character('b')
                .build()
        val SET_ALL_CHAR = TextCharacterBuilder.newBuilder()
                .character('c')
                .build()
        val TO_COPY = arrayOf(arrayOf(TO_COPY_CHAR))
        val IMAGE_TO_COPY = DefaultTextImage(Size.ONE, arrayOf(arrayOf()), SET_ALL_CHAR)
        val IMAGE_TO_COPY_AND_CROP = DefaultTextImage(Size(2, 2), arrayOf(arrayOf()), SET_ALL_CHAR)

    }

}