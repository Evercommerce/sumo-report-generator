package com.sumologic.report.generator.excel;

import com.sumologic.report.config.ReportConfig;
import com.sumologic.report.config.ReportSheet;
import com.sumologic.service.SumoDataService;
import com.sumologic.service.SumoDataServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

@Service
public class ExcelWorkbookPopulator implements WorkbookPopulator {

    private static final Log LOGGER = LogFactory.getLog(ExcelWorkbookPopulator.class);

    @Autowired
    private SumoDataServiceFactory sumoDataServiceFactory;

    @Autowired
    private WorksheetPopulator worksheetPopulator;

    @Override
    public void populateWorkbookWithData(ReportConfig reportConfig, Workbook workbook) throws IOException {
        openWorkbookAndProcessSheets(reportConfig, workbook);
    }

    private void openWorkbookAndProcessSheets(ReportConfig reportConfig, Workbook workbook) throws IOException {
        LOGGER.debug("populating workbook");
        processSheets(reportConfig, workbook);
        LOGGER.debug("workbook populated");
    }

    private void processSheets(ReportConfig reportConfig, Workbook workbook) throws IOException {
        SumoDataService sumoDataService = sumoDataServiceFactory.getSumoDataService(reportConfig);
        for (ReportSheet reportSheet : reportConfig.getReportSheets()) {
            processSheet(workbook, sumoDataService, reportSheet);
            FileOutputStream fileOut = new FileOutputStream(reportConfig.getDestinationFile());
            workbook.write(fileOut);
            fileOut.close();
        }
    }

    private void processSheet(Workbook workbook, SumoDataService sumoDataService, ReportSheet reportSheet) {
        LOGGER.info("populating sheet " + reportSheet.getSheetName());
        String worksheetName = WorkbookUtil.createSafeSheetName(reportSheet.getSheetName());
        Sheet workbookSheet = workbook.getSheet(worksheetName);
        worksheetPopulator.populateSheetWithData(workbookSheet, reportSheet, sumoDataService);
    }

}