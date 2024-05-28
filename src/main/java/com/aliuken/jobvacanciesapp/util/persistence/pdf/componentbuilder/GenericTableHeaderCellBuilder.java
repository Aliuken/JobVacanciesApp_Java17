package com.aliuken.jobvacanciesapp.util.persistence.pdf.componentbuilder;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

public class GenericTableHeaderCellBuilder implements PdfPCellEvent {
	private static final float TEXT_PADDING = 5f;
	private static final Font FONT = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
	private static final int ARC_CORNER_RADIUS = 7;
	private static final BaseColor COLOR = new BaseColor(215, 215, 215);
	private static final float HEIGHT = 15;

	private final Paragraph paragraph;
	private final float width;
	private final PdfPCell defaultCell;

	public GenericTableHeaderCellBuilder(final String headerContent, final PdfPCell defaultCell) {
		this.paragraph = new Paragraph(headerContent, FONT);
		this.paragraph.setAlignment(Element.ALIGN_CENTER);
		this.width = GenericTableHeaderCellBuilder.getWidth(paragraph);
		this.defaultCell = defaultCell;
	}

	private static float getWidth(Paragraph paragraph) {
		float width = 0 ;
		for(final Element element : paragraph) {
			for(final Chunk chunk : element.getChunks()) {
				float textWidth = chunk.getWidthPoint();
				width = textWidth + 2 * TEXT_PADDING;
				break;
			}
			break;
		}
		return width;
	}

	@Override
	public final void cellLayout(final PdfPCell cell, final Rectangle position, final PdfContentByte[] canvases) {
		final PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
		canvas.saveState();
		canvas.setColorFill(COLOR);
		canvas.setColorStroke(COLOR);

		final float x = (position.getLeft() + position.getRight() - width) / 2;
		final float y = position.getTop() - HEIGHT - ARC_CORNER_RADIUS;
		canvas.roundRectangle(x, y, width, HEIGHT, ARC_CORNER_RADIUS);

		canvas.fillStroke();
		canvas.restoreState();
	}

	public PdfPCell build() {
		final PdfPCell cell = new PdfPCell(defaultCell);
		cell.addElement(paragraph);
		cell.setCellEvent(this);
		cell.setFixedHeight(HEIGHT + TEXT_PADDING);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setPaddingBottom(3f);
		cell.setBorder(2);
		cell.setBorderColor(BaseColor.RED);
		return cell;
	}
}
