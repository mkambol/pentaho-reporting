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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import junit.framework.Assert;
import net.sourceforge.barbecue.env.HeadlessEnvironment;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.libraries.base.util.IOUtils;

public class ShortTest extends GoldTestBase
{
  public ShortTest()
  {
  }

  @Test
  public void testOne() throws Exception
  {
    final File file = new File("./test-gold/reports/Prd-3514.prpt");

    final MasterReport originalReport = parseReport(file);
    final MasterReport tunedReport = tuneForTesting(originalReport);
    MasterReport report = postProcess(tunedReport);

    report = tuneForLegacyMode(report);

    final String fileName = IOUtils.getInstance().stripFileExtension(file.getName());

    File stream = new File(file.getParent(), fileName + "-table-stream-TEST.xml");
    File flow =new File(file.getParent(), fileName + "-table-flow-TEST.xml")  ;
    File table =new File(file.getParent(), fileName + "-table-page-TEST.xml");
    File page =new File(file.getParent(), fileName + "-page-TEST.xml");

    stream.delete();
    flow.delete();
    table.delete();
    page.delete();

    outputFile(executeTableStream(report), stream);
    outputFile(executeTableFlow(report), flow);
    outputFile(executeTablePage(report), table);
    outputFile(executePageable(report), page);
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


}
