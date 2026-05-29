package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Class that handles reading Excel files
 */

public class XLSreader {

	final XSSFWorkbook wb;
	final String filePath;

	/**
	 * Opens an excel workbook to be read from and to write to
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	
	public XLSreader(String filePath) throws IOException {
		InputStream inp = new FileInputStream(filePath);	
		wb = new XSSFWorkbook(inp);
		this.filePath = filePath;
	}
	
	/**
	 * Returns the string value of the chosen cell
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public String readCell(int rowIndex, int columnIndex) throws FileNotFoundException, IOException {

		XSSFCell cell = wb.getSheetAt(0).getRow(rowIndex).getCell(columnIndex);
		String output = "";
		try {
			switch (cell.getCellType()) {

			case STRING:
				output = cell.getRichStringCellValue().getString();
				break;

			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					output = cell.getDateCellValue().toString();
				} else {
					output = Double.toString(cell.getNumericCellValue());
				}
				break;

			case BOOLEAN:
				output = Boolean.toString(cell.getBooleanCellValue());
				break;

			case FORMULA:
				XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
				CellValue tempValue = evaluator.evaluate(cell);
				output = tempValue.formatAsString();
				break;

			case BLANK:
				break;

			default:
				break;
			}		
		} catch (NullPointerException e) {

		} 
		return output;

	}
	
	/**
	 * Writes the provided value to the chosen cell
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @param value
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public void writeCell(int rowIndex, int columnIndex, String value) throws FileNotFoundException, IOException {
		XSSFCell cell = wb.getSheetAt(0).getRow(rowIndex).getCell(columnIndex);
		cell.setCellValue(value);	
		FileOutputStream outputStream = new FileOutputStream(filePath);
		wb.write(outputStream);
		outputStream.close();
	}

	/**
	 * Closes the connection to the workbook
	 * @throws IOException
	 */
	public void close() throws IOException {
		wb.close();
	}

	/**
	 * Creates a template file at the specified folder
	 * @param folder
	 * @throws IOException
	 */

	public static void createTemplate(String filePath, int speciesN, boolean sexDif, String type, boolean speciesAreVertical) throws IOException {
		if (speciesAreVertical) {
			createTemplateVertical(filePath,speciesN,sexDif,type);
		} else {
			createTemplateHorizontal(filePath,speciesN,sexDif,type);
		}		
	}
	
	/**
	 * Private method to handle creating the vertical template
	 * @param filePath
	 * @param speciesN
	 * @param sexDif
	 * @param type
	 * @throws IOException
	 */

	private static void createTemplateVertical(String filePath, int speciesN, boolean sexDif, String type) throws IOException {

		//create a workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//create a sheet in the workbook(you can give it a name)
		XSSFSheet sheet = workbook.createSheet("excel-sheet");

		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);

		//create a row in the sheet
		XSSFRow row = sheet.createRow(0);		
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("Species");

		int celln =1;

		cell = row.createCell(celln);
		cell.setCellValue("Sex");	
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Sex Ratio");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Survival Rate");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Infras.-Induced Mortality");
		cell.setCellStyle(style);	
		if (type.contentEquals("Spatial")) {
			celln++;
			cell = row.createCell(celln);
			cell.setCellValue("Max Dispersal Length");
		}
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Longevity");	
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Life Phase Change");	
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Minimum Offspring Number");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Maximum Offspring Number");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Average Offspring Number");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		if (type.contentEquals("Spatial")) {
			cell.setCellValue("Population Density");
		} else {
			cell.setCellValue("Starting Population");
		}
		cell.setCellStyle(style);	
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Maximum Population");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Age at First Birth");	
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Min Interval Between Births");
		cell.setCellStyle(style);
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Average Interval Between Births");
		celln++;
		cell = row.createCell(celln);
		cell.setCellValue("Birth Rate");
		cell.setCellStyle(style);
		if (type.contentEquals("Spatial")) {
			celln++;
			cell = row.createCell(celln);
			cell.setCellValue("Mate Finding Radius");
			cell.setCellStyle(style);			
		}

		if (sexDif) {
			speciesN = speciesN*2;
		}

		for (int species = 0 ; species < speciesN ; species++) {
			row = sheet.createRow(species+1);
			cell = row.createCell(0);
			cell.setCellValue("Species " + ((species)/(sexDif?2:1)+1) );
			XSSFCellStyle centered = workbook.createCellStyle();
			centered.setVerticalAlignment(VerticalAlignment.CENTER);
			cell.setCellStyle(centered);
			cell = row.createCell(1);			
			cell.setCellValue(sexDif ? "Male":"NA");	
			cell.setCellStyle(style);
			for (int i = 2; i < celln+1; i++) {
				cell = row.createCell(i);
				cell.setCellStyle(style);
			}	

			if (sexDif) { //If different creates column for females too
				species++;
				row = sheet.createRow(species+1);
				row.createCell(0);
				cell = row.createCell(1);
				sheet.addMergedRegion(new CellRangeAddress(species, species+1, 0, 0));
				cell.setCellValue("Female");	
				cell.setCellStyle(style);
				for (int i = 2; i < celln+1; i++) {
					cell = row.createCell(i);
					cell.setCellStyle(style);
					if (i > 4) {
						sheet.addMergedRegion(new CellRangeAddress(species, species+1, i, i));
					}
				}	
			}			
		}	

		sheet.setColumnWidth(0, 256*36);

		//save the Excel file
		File file = new File(filePath);

		// Check if the file already exists
		if (file.exists()) {
		    String parentPath = file.getParent();
		    String fileName = file.getName();
		    
		    String baseName = fileName;
		    String extension = "";

		    // Find the last dot to separate the name and the extension (e.g., .xlsx)
		    int dotIndex = fileName.lastIndexOf('.');
		    if (dotIndex > 0) {
		        baseName = fileName.substring(0, dotIndex);
		        extension = fileName.substring(dotIndex);
		    }

		    int counter = 1;
		    // Loop until we find a filename that doesn't exist
		    while (file.exists()) {
		        String newName = baseName + " (" + counter + ")" + extension;
		        // Reconstruct the file path
		        if (parentPath != null) {
		            file = new File(parentPath, newName);
		        } else {
		            file = new File(newName);
		        }
		        counter++;
		    }
		}

		// Save the Excel file using try-with-resources to ensure it closes properly
		try (FileOutputStream out = new FileOutputStream(file)) {
		    workbook.write(out);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		workbook.close();
	}

	/**
	 * Private method to handle creating the horizontal template
	 * @param filePath
	 * @param speciesN
	 * @param sexDif
	 * @param type
	 * @throws IOException
	 */
	private static void createTemplateHorizontal(String filePath, int speciesN, boolean sexDif, String type) throws IOException {

		//create a workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//create a sheet in the workbook(you can give it a name)
		XSSFSheet sheet = workbook.createSheet("excel-sheet");

		XSSFCellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);

		//create a row in the sheet
		XSSFRow row = sheet.createRow(0);		
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("Species");

		int celln = 1;

		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Sex");	
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Sex Ratio");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Survival Rate");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Infras.-Induced Mortality");
		cell.setCellStyle(style);
		if (type.contentEquals("Spatial")) {
			celln++;
			row = sheet.createRow(celln);
			cell = row.createCell(0);
			cell.setCellValue("Max Dispersal Length");
			cell.setCellStyle(style);
		}
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Longevity");	
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Life Phase Change");	
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Minimum Offspring Number");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Maximum Offspring Number");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Average Offspring Number");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		if (type.contentEquals("Spatial")) {
			cell.setCellValue("Population Density");
		} else {
			cell.setCellValue("Starting Population");
		}
		cell.setCellStyle(style);	
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Maximum Population");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Age at First Birth");	
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Min Interval Between Births");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Average Interval Between Births");
		cell.setCellStyle(style);
		celln++;
		row = sheet.createRow(celln);
		cell = row.createCell(0);
		cell.setCellValue("Birth Rate");
		cell.setCellStyle(style);
		if (type.contentEquals("Spatial")) {
			celln++;
			row = sheet.createRow(celln);
			cell = row.createCell(0);
			cell.setCellValue("Mate Finding Radius");
			cell.setCellStyle(style);
		}

		if (sexDif) {
			speciesN = speciesN*2;
		}

		XSSFCellStyle centered = workbook.createCellStyle();
		centered.setAlignment(HorizontalAlignment.CENTER);
		centered.setBorderTop(BorderStyle.THIN);
		centered.setBorderBottom(BorderStyle.THIN);
		centered.setBorderLeft(BorderStyle.THIN);
		centered.setBorderRight(BorderStyle.THIN);

		for (int species = 0 ; species < speciesN ; species++) {
			row = sheet.getRow(0);
			cell = row.createCell(species+1);
			cell.setCellValue("Species " + ((species)/(sexDif?2:1)+1) );
			cell.setCellStyle(centered);
			row = sheet.getRow(1);
			cell = row.createCell(species+1);			
			cell.setCellValue(sexDif ? "Male":"NA");	
			cell.setCellStyle(style);

			for (int i = 2; i < celln+1; i++) {
				row = sheet.getRow(i);
				cell = row.createCell(species+1);
				cell.setCellStyle(style);				
			}	

			if (sexDif) { //If different creates column for females too
				species++;

				row = sheet.getRow(0);
				cell = row.createCell(species+1);
				cell.setCellStyle(style);
				sheet.addMergedRegion(new CellRangeAddress(0,0 , species, species+1));
				row = sheet.getRow(1);
				cell = row.createCell(species+1);		
				cell.setCellValue("Female");	
				cell.setCellStyle(style);
				for (int i = 2; i < celln+1; i++) {
					row = sheet.getRow(i);
					cell = row.createCell(species+1);
					cell.setCellStyle(style);
					if (i > 4) {
						cell.setCellStyle(centered);
						sheet.addMergedRegion(new CellRangeAddress(i, i, species, species+1));						
					}
				}	
			}			
		}	

		sheet.setColumnWidth(0, 256*36);

		//save the Excel file
		File file = new File(filePath);

		// Check if the file already exists
		if (file.exists()) {
		    String parentPath = file.getParent();
		    String fileName = file.getName();
		    
		    String baseName = fileName;
		    String extension = "";

		    // Find the last dot to separate the name and the extension (e.g., .xlsx)
		    int dotIndex = fileName.lastIndexOf('.');
		    if (dotIndex > 0) {
		        baseName = fileName.substring(0, dotIndex);
		        extension = fileName.substring(dotIndex);
		    }

		    int counter = 1;
		    // Loop until we find a filename that doesn't exist
		    while (file.exists()) {
		        String newName = baseName + " (" + counter + ")" + extension;
		        // Reconstruct the file path
		        if (parentPath != null) {
		            file = new File(parentPath, newName);
		        } else {
		            file = new File(newName);
		        }
		        counter++;
		    }
		}

		// Save the Excel file using try-with-resources to ensure it closes properly
		try (FileOutputStream out = new FileOutputStream(file)) {
		    workbook.write(out);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		workbook.close();
	}

	/**
	 * Gets the row of the first term that matches from the ones given
	 * @param term
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public int getTermRow(String... term) throws FileNotFoundException, IOException {
		for (int i = 0 ; i < term.length; i++) {
			for (int row = 0; row < 50; row++) {		
				try {
					if (readCell(row, 0).toLowerCase().contains(term[i].toLowerCase())) {				
						return row;
					} } catch (NullPointerException e) {
						break;
					}
			}
		}
		return -1;
	}
	
	/**
	 * Gets the column of the first term that matches from the ones given
	 * @param term
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public int getTermCol(String... term) throws FileNotFoundException, IOException {
		for (int i = 0 ; i < term.length; i++) {
			for (int col = 0; col < 50; col++) {		
				try {
					if (readCell(0, col).toLowerCase().contains(term[i].toLowerCase())) {				
						return col;
					} } catch (NullPointerException e) {
						break;
					}
			}
		}
		return -1;
	}


}