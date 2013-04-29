/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2000 - 2011 Pentaho Corporation and Contributors...  
 * All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertNotNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.FileModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.output.ReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FlowHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal.XmlTableOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.boot.PackageManager;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;

public class ShortTest extends GoldTestBase
{

  private static final Log LOGGER = LogFactory.getLog(PackageManager.class);

  public ShortTest()
  {
  }

  @Test
  public void testOne() throws Exception
  {

    final File file = new File("./test-gold/reports/Prd-3514.prpt");


    MasterReport originalReport = parseReport(file);
    MasterReport tunedReport = tuneForTesting(originalReport);
    MasterReport report = postProcess(tunedReport);

    report = tuneForLegacyMode(report);

    final String fileName = IOUtils.getInstance().stripFileExtension(file.getName());

    executeAndOutputReport(file, report, fileName);

   // originalReport = parseReport(file);
   // tunedReport = tuneForTesting(originalReport);
   // report = serializeDeserialize(tunedReport);
   // report = tuneForLegacyMode(report);

   // executeAndOutputReport(file, report, "SERIALIZE-" + fileName);
  }

  private void executeAndOutputReport(final File file,
                                      final MasterReport report,
                                      final String fileName) throws Exception
  {
    File stream = new File(file.getParent(), fileName + "-table-stream-TEST.xml");
    File flow =new File(file.getParent(), fileName + "-table-flow-TEST.xml")  ;
    File table =new File(file.getParent(), fileName + "-table-page-TEST.xml");
    File page =new File(file.getParent(), fileName + "-page-TEST.xml");
    File html =new File(file.getParent(), fileName + "-html-TEST.zip");

    stream.delete();
    flow.delete();
    table.delete();
    page.delete();

    FileModelPrinter fmp = new FileModelPrinter("model-");


    //outputFile(executeTableStream(report), stream);
    //outputFile(executeTableFlow(report), flow);
    //outputFile(executeTablePage(report), table);
    outputFile(executePageable(report), page);
    //OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(html));
    //HtmlReportUtil.createZIPHTML(report, outputStream, "prd-3514.html");

  }

  protected MasterReport serializeDeserialize(final MasterReport originalReport) throws Exception
  {
    //  if (true) return originalReport;
    final byte[] bytes = serializeReportObject(originalReport);
    final MasterReport report = deserializeReportObject(bytes);
    return report;
  }

  private byte[] serializeReportObject(final MasterReport report) throws IOException
  {
    // we don't test whether our demo models are serializable :)
    // clear all report properties, which may cause trouble ...
    final MemoryByteArrayOutputStream bo = new MemoryByteArrayOutputStream();
    final ObjectOutputStream oout = new ObjectOutputStream(bo);
    oout.writeObject(report);
    oout.close();
    return bo.toByteArray();
  }

  private MasterReport deserializeReportObject(final byte[] data) throws IOException, ClassNotFoundException
  {
    final ByteArrayInputStream bin = new ByteArrayInputStream(data);
    final ObjectInputStream oin = new ObjectInputStream(bin);
    final MasterReport report2 = (MasterReport) oin.readObject();
    assertNotNull(report2);
    return report2;
  }

  public void outputFile(final byte[] reportOutput, final File file) throws Exception
  {
    final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
    try
    {
      outputStream.write(reportOutput);
    }
    finally
    {
      outputStream.close();
    }
  }

  protected byte[] executeHtmlStream(final MasterReport report)
      throws IOException, ReportProcessingException
  {
    final MemoryByteArrayOutputStream outputStream = new MemoryByteArrayOutputStream();
    try
    {
      HtmlOutputProcessor processor = new FlowHtmlOutputProcessor();
      final ReportProcessor streamReportProcessor = new StreamReportProcessor(report, processor);
      try
      {
        streamReportProcessor.processReport();
      }
      finally
      {
        streamReportProcessor.close();
      }
    }
    finally
    {
      outputStream.close();
    }
    return (outputStream.toByteArray());
  }

}
