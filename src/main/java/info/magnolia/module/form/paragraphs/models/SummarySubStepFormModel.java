/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.module.form.paragraphs.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;


import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.engine.FormState;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.paragraphs.models.multistep.NavigationUtils;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;

/**
 * Model for summary paragraph, displays a list of parameters submitted by the form.
 */
public class SummarySubStepFormModel extends SubStepFormModel {

    public SummarySubStepFormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    public List<SummaryFormStepBean> getSummaryFormStepBeanList() throws AccessDeniedException, PathNotFoundException, RepositoryException {
        List<SummaryFormStepBean> summaryFormStepBeanList = new ArrayList<SummaryFormStepBean>();
        
        FormState formState = this.getFormState();
        if(formState != null) {
            Map<String, FormStepState> steps = this.getFormState().getSteps();
            
            for (FormStepState step : steps.values()) {
                SummaryFormStepBean summaryFormStepBean = createSummaryFormStepBean(step);
                if(summaryFormStepBean != null) {
                    summaryFormStepBeanList.add(summaryFormStepBean);
                }
            }
        }
        return summaryFormStepBeanList;
    }

    protected SummaryFormStepBean createSummaryFormStepBean(FormStepState step) {
        Map<String, Object> parameters = step.getValues();
        Map<String, Object> templateParams = new LinkedHashMap<String, Object>();
        try {
            if(!parameters.isEmpty()) {
                SummaryFormStepBean summaryFormStepBean = new SummaryFormStepBean();
                String paragraphUUID = step.getParagraphUuid();
                Content paragraph = ContentUtil.getContentByUUID(ContentRepository.WEBSITE, paragraphUUID);
                Content page = getFormStepPage(paragraph);
                summaryFormStepBean.setName(page.getName());
                summaryFormStepBean.setTitle(page.getTitle());
                
                List<String> fieldList = findStepFieldNames(paragraph);
                
                for(String controlName: fieldList) {
                    if(parameters.containsKey(controlName)) {
                        templateParams.put(controlName, parameters.get(controlName));
                    }
                }
                summaryFormStepBean.setParameters(templateParams);
                return summaryFormStepBean;
            }
        } catch (Exception e) {
            throw new IllegalStateException("SummaryParagraph could not process the parameters");
        }
        return null;
    }

    private Content getFormStepPage(Content paragraph) throws AccessDeniedException, PathNotFoundException, RepositoryException {
        
        Content page = paragraph;
        
        while(!page.getItemType().getSystemName().equals(ItemType.CONTENT.getSystemName()) ) {
            page = page.getParent();
        }
        
        return page;
    }

    protected Content getFormStartPage() throws PathNotFoundException, RepositoryException, AccessDeniedException {
        Content startPage;
        
        startPage = MgnlContext.getAggregationState().getMainContent().getParent();

        Content startParagraphNode = NavigationUtils.findParagraphOfType(startPage, FormParagraph.class);

        if (startParagraphNode == null) {
            // Ideally we would return a view that describes the problem and how to resolve it
            throw new IllegalStateException("FormStepParagraph on page [" + content.getHandle() + "] could not find a FormParagraph in its parent");
        }
        return startParagraphNode;
    }

    protected List<String> findStepFieldNames(Content paragraph) {
        List<String> fieldList = new ArrayList<String>();
        Collection<Content> fieldDefinitionList = ContentUtil.collectAllChildren(paragraph, ItemType.CONTENTNODE);
        CollectionUtils.collect(fieldDefinitionList, new Transformer() {

            public Object transform(Object input) {
                Content node = (Content) input;
                String template = node.getTemplate();
                String controlName = NodeDataUtil.getString(node, "controlName");
                if(StringUtils.isNotEmpty(controlName) && StringUtils.isNotEmpty(template) && !StringUtils.equals(template, "formHidden")) {
                    return controlName;
                }
                return null;
            }
            
        }, fieldList);
        //remove nulls
        CollectionUtils.filter(fieldList, new Predicate() {

            public boolean evaluate(Object object) {
                if(object == null) {
                    return false;
                }
                return true;
            }
            
        });
        return fieldList;
    }
}
