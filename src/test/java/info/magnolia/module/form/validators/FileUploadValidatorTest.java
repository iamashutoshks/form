/**
 * This file Copyright (c) 2015-2017 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form.validators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A class to test {@link FileUploadValidator}.
 */
public class FileUploadValidatorTest {

    private static final long maxFileSize = 10485760;
    private static final String fileName = "businessHeadShot";

    private static final String mimeType_JPG = "image/jpg";
    private static final String mimeType_PNG = "image/png";
    private static final String mimeType_PDF = "application/pdf";

    private static final String errorMessageKey_invalidMimeType = "form.user.errorMessage.fileUpload.invalidMimeType";
    private static final String errorMessageKey_fileTooBig = "form.user.errorMessage.fileUpload.fileTooBig";


    private FileUploadValidator validator;
    private Map<String, Document> uploadedFiles;

    @Before
    public void setUp() throws Exception {
        final WebContext webContext = mock(WebContext.class);
        MgnlContext.setInstance(webContext);
        MultipartForm multipartForm = mock(MultipartForm.class);
        when(webContext.getPostedForm()).thenReturn(multipartForm);
        uploadedFiles = new HashMap<String, Document>();
        when(multipartForm.getDocuments()).thenReturn(uploadedFiles);
        validator = new FileUploadValidator();
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
        validator = null;
    }

    @Test
    public void testMimeTypeValidationNoTypesDefined() {
        // given
        uploadedFiles.put(fileName, createUploadFile("dilbert.jpg", 1500, mimeType_JPG));

        // when
        ValidationResult validationResult = validator.validateWithResult(null, fileName);

        // then
        assertTrue(validationResult.isSuccess());
    }

    @Test
    public void testMimeTypeValidationWithTypeMatchingDefinition() {
        // given
        validator.setAllowedMimeTypes(Arrays.asList(mimeType_JPG, mimeType_PDF));
        uploadedFiles.put(fileName, createUploadFile("dilbert.jpg", 1500, mimeType_JPG));

        // when
        ValidationResult validationResult = validator.validateWithResult(null, fileName);

        // then
        assertTrue(validationResult.isSuccess());
    }

    @Test
    public void testMimeTypeValidationWithTypeNotMatchingDefinition() {
        // given
        validator.setAllowedMimeTypes(Arrays.asList(mimeType_JPG, mimeType_PDF));
        uploadedFiles.put(fileName, createUploadFile("dogbert.png", 1500, mimeType_PNG));

        // when
        ValidationResult validationResult = validator.validateWithResult(null, fileName);

        // then
        assertFalse(validationResult.isSuccess());
        assertEquals(errorMessageKey_invalidMimeType, validationResult.getErrorMessage());
    }


    @Test
    public void testMaxFileSizeValidationIsOk() {
        // given
        uploadedFiles.put(fileName, createUploadFile("dogbert.png", maxFileSize, mimeType_PNG));

        // when
        ValidationResult validationResult = validator.validateWithResult(null, fileName);

        // then
        assertTrue(validationResult.isSuccess());
    }

    @Test
    public void testMaxFileSizeValidationFileTooBig() {
        // given
        uploadedFiles.put(fileName, createUploadFile("dogbert.png", maxFileSize + 42, mimeType_PNG));

        // when
        ValidationResult validationResult = validator.validateWithResult(null, fileName);

        // then
        assertFalse(validationResult.isSuccess());
        assertEquals(errorMessageKey_fileTooBig, validationResult.getErrorMessage());
    }


    /**
     * Creates a {@link Document} using a mocked {@link File}.
     */
    private Document createUploadFile(String fileName, final long fileSize, String mimeType) {
        File file = mock(File.class);
        when(file.getName()).thenReturn(fileName);

        return new Document(file, mimeType) {
            @Override
            public long getLength() {
                return fileSize;
            }
        };
    }


}
