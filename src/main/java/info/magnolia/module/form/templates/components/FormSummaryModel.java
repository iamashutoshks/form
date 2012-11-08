/**
 * This file Copyright (c) 2010-2012 Magnolia International
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
package info.magnolia.module.form.templates.components;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.form.engine.FormState;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.templates.components.multistep.NavigationUtils;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Model for summary paragraph, displays a list of parameters submitted by the form.
 * I the option onlyLast is selected, only the parameters of the previous step will be displayed.
 *
 * @param <RD> - an instance of {@link RenderableDefinition}
 *
 * @version $Id$
 */
public class FormSummaryModel<RD extends RenderableDefinition> extends RenderingModelImpl<RD> {

    protected FormState formState;
    protected final TemplatingFunctions functions;
    private RenderingContext context;

    @Inject
    public FormSummaryModel(Node content, RD definition, RenderingModel<?> parent, TemplatingFunctions functions, RenderingContext context) {
        super(content, definition, parent);
        this.functions = functions;
        formState = findFormState();
        this.context = context;
    }

    public List<FormSummaryBean> getFormSummaryBeanList() throws AccessDeniedException, PathNotFoundException, RepositoryException {

        List<FormSummaryBean> summaryFormStepBeanList = new ArrayList<FormSummaryBean>();
        boolean onlyLast = isDisplayOnlyLastStep();
        ArrayList<FormStepState> steps = getSteps();

        for (int index = 0; index < steps.size() ; index++) {
            FormStepState step = steps.get(index);
            if(!onlyLast || (onlyLast && index == steps.size() -1)) {
                FormSummaryBean formSummaryBean = createFormSummaryBean(step);
                if(formSummaryBean != null) {
                    summaryFormStepBeanList.add(formSummaryBean);
                }
            }
        }

        return summaryFormStepBeanList;
    }

    protected ArrayList<FormStepState> getSteps() throws RepositoryException {
        Node currentPage = context.getMainContent();
        Node currentStepContent = NavigationUtils.findParagraphOfType(currentPage, FormStepParagraph.class);
        ArrayList<FormStepState> steps = new ArrayList<FormStepState>();
        if(formState != null) {
            Iterator<FormStepState> stepsIt = formState.getSteps().values().iterator();
            while (stepsIt.hasNext()) {
                FormStepState step = stepsIt.next();
                if(step.getParagraphUuid().equals(NodeUtil.getNodeIdentifierIfPossible(currentStepContent))) {
                    break;
                }
                steps.add(step);
            }
        }
        return steps;
    }

    protected FormState findFormState() {
        RenderingModel oparent = getParent();
        while(oparent != null && !(oparent instanceof SubStepFormModel)) {
            oparent = oparent.getParent();
        }
        if(oparent != null) {
            return ((SubStepFormModel) oparent).getFormState();
        }
        return null;
    }

    protected boolean isDisplayOnlyLastStep() {

        return NodeDataUtil.getBoolean(ContentUtil.asContent(content), "onlyLast", false);
    }

    protected FormSummaryBean createFormSummaryBean(FormStepState step) {
        Map<String, Object> stepParameters = step.getValues();
        Map<String, Object> templateParams = new LinkedHashMap<String, Object>();
        FormSummaryBean summaryFormStepBean = null;
        try {
            if(!stepParameters.isEmpty()) {
                String paragraphUUID = step.getParagraphUuid();
                Node contentParagraph = NodeUtil.getNodeByIdentifier(RepositoryConstants.WEBSITE, paragraphUUID);

                //FIXME I18nContentWrapper has to be migrated
                Content page = new I18nContentWrapper(ContentUtil.asContent(NavigationUtils.findParagraphParentPage(contentParagraph)));

                Collection<Content> contentParagraphFieldList = findContentParagraphFields(ContentUtil.asContent(contentParagraph));

                for(Content fieldNode: contentParagraphFieldList) {
                    String controlName = NodeDataUtil.getString(fieldNode, "controlName");
                    if(stepParameters.containsKey(controlName)) {
                        findAndSetTemplateParameters(fieldNode, stepParameters, controlName, templateParams);
                    }
                }
                if(!templateParams.isEmpty()) {
                    summaryFormStepBean = new FormSummaryBean();
                    summaryFormStepBean.setParameters(templateParams);
                    summaryFormStepBean.setName(page.getName());
                    summaryFormStepBean.setTitle(NodeDataUtil.getString(page, "navigationTitle", page.getTitle()));
                }
                return summaryFormStepBean;
            }
        } catch (Exception e) {
            throw new IllegalStateException("SummaryParagraph could not process the parameters");
        }
        return null;
    }

    protected void findAndSetTemplateParameters(Content fieldNode, Map<String, Object> stepParameters, String controlName, Map<String, Object> templateParams) {
        Content i18nFieldNode = new I18nContentWrapper(fieldNode);
        String title = i18nFieldNode.getTitle();
        if (StringUtils.isNotEmpty(NodeDataUtil.getString(i18nFieldNode, "labels"))) {
            findAndSetComplexControlLabels(i18nFieldNode, stepParameters, templateParams, controlName);
        } else if (StringUtils.isNotEmpty(title)) {
            String value = (String) stepParameters.get(controlName);
            if(StringUtils.isNotEmpty(value)) {
                templateParams.put(title, value);
            }
        }
    }

    /**
     * Controls like checkbox, select dont have title, need to find the label of the option/s selected.
     */
    protected void findAndSetComplexControlLabels(Content fieldNode, Map<String, Object> stepParameters,
            Map<String, Object> templateParams, String controlName) {
        Content i18nFieldNode = new I18nContentWrapper(fieldNode);
        String stepParamvalue = (String) stepParameters.get(controlName);
        if(StringUtils.isNotEmpty(stepParamvalue)) {
            String[] stepfieldValues = stepParamvalue.split("_");
            Map<String, String> controlValueLabelMap = fillControlValueLabelMap(i18nFieldNode);
            for (String value : stepfieldValues) {
                if(!value.equals("")) {
                    templateParams.put(controlValueLabelMap.get(value), "*");
                }
            }
        }
    }

    private Map<String, String> fillControlValueLabelMap(Content fieldNode) {
        Map<String, String> controlValueLabelMap = new HashMap<String,String>();
        String controlLabels = NodeDataUtil.getString(fieldNode, "labels");
        String[] labelsArray = controlLabels.split("\r\n");
        for (String controlLabelValue : labelsArray) {
            String[] labelValueArray = controlLabelValue.split(":");
            if(labelValueArray.length > 0) {
                String label = labelValueArray[0];
                String value = label;
                if(labelValueArray.length > 1) {
                    value = labelValueArray[1];
                }
                controlValueLabelMap.put(value, label);
            }
        }//end for
        return controlValueLabelMap;
    }

    protected Collection<Content> findContentParagraphFields(Content contentParagraph) {

        String query = "select * from " + ItemType.CONTENTNODE + " where jcr:path like '"
                + contentParagraph.getHandle() +"/%' and controlName is not null";
        return QueryUtil.query(ContentRepository.WEBSITE, query);
    }

}
