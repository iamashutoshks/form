/**
 * This file Copyright (c) 2015 Magnolia International
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

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Validator} class for file upload fields to validate against a list of allowed mime types and a maxFileSize.
 */
public class FileUploadValidator extends ExtendedValidator {

    private static final Logger log = LoggerFactory.getLogger(FileUploadValidator.class);

    /**
     * Maximum allowed file size for uploaded file in bytes (defaults to 10MB).
     */
    private long maxFileSize = 10485760;

    /**
     * List of allowed mime types for uploaded file. If not defined all mime types are allowed.
     */
    private List<String> allowedMimeTypes;

    @Override
    public ValidationResult validateWithResult(String value, String fieldName) {

        // Check if the form contains a file upload.
        // Since the context is always a webcontext, it's fine to cast it.
        MultipartForm form = ((WebContext) MgnlContext.getInstance()).getPostedForm();
        Map<String, Document> fileMap = form.getDocuments();

        boolean validationSuccess = false;
        if (fileMap.containsKey(fieldName)) {
            Document uploadedFile = fileMap.get(fieldName);
            log.debug("File uploaded: {}", uploadedFile.getFileNameWithExtension());

            // invalid mime type
            if ((null != allowedMimeTypes) && !allowedMimeTypes.contains(uploadedFile.getType())) {
                log.info("The uploaded file '" + uploadedFile.getFileNameWithExtension() + "' has an unsupported/illegal mime type: '" + uploadedFile.getType() + "'.");
                setErrorMessage("form.user.errorMessage.fileUpload.invalidMimeType");
            }
            // upload is too big
            else if (uploadedFile.getLength() > maxFileSize) {
                log.info("The uploaded file '" + uploadedFile.getFileNameWithExtension() +
                        "' is of size " + uploadedFile.getLength() + " bytes and this is larger than the maximum allowed file size: " + maxFileSize);
                setErrorMessage("form.user.errorMessage.fileUpload.fileTooBig");
            } else {
                validationSuccess = true;
            }
        }

        return new ValidationResult(validationSuccess, getErrorMessage());
    }

    /**
     * Returns the maximal allowed length of the uploaded file in bytes.
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * Returns the list of the allowed mime types of the uploaded file.
     */
    public List<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

}
