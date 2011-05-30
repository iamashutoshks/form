[#assign cms=JspTaglibs["cms-taglib"]]

[#if actionResult == "go-to-first-page"]
	<div class="text">
		${i18n.get("form.user.errorMessage.go-to-first-page", [mgnl.createLink("website", model.view.firstPage)])}
	</div>
[#elseif actionResult == "success"]
    <div class="text success">
        <h1>${model.view.successTitle!i18n['form.default.successTitle']}</h1>
        <p>${model.view.successMessage!}</p>
    </div>
[#elseif actionResult == "session-expired"]
	<div class="text error">
		${i18n.get("form.user.errorMessage.session-expired", [mgnl.createLink("website", model.view.firstPage)])}
	</div>
[#elseif actionResult == "failure"]
	<div class="text error">
		<ul>
			<li>${model.view.errorMessage}</li>
		</ul>
	</div>
[#else]
    [#if model.view.validationErrors?size > 0]
        <div class="text error">
            <h1>${model.view.errorTitle!i18n['form.default.errorTitle']}</h1>
            <ul>
                [#assign keys = model.view.validationErrors?keys]
                [#list keys as key]
                    <li>
                        <a href="#${key}_label">${model.view.validationErrors[key]}</a>
                    </li>
                [/#list]
            </ul>
        </div>
    [/#if]
    [#if mgnl.editMode]
        [#if content.@name == "singleton"]
    <div style="clear: both" >
        [@cms.editBar editLabel="${i18n['form.editLabel']}" moveLabel="" deleteLabel="" /]
    </div>
        [#else]
            <div style="clear: both" >
            [@cms.editBar editLabel="${i18n['form.editLabel']}" moveLabel="" /]
            </div>
        [/#if]
    [/#if]
    <div class="text">
        <h1>${mgnl.encode(content).formTitle!}</h1>
        <p>${mgnl.encode(content).formText!}</p>
    </div>
    <div class="text">
    
    	[#assign summaryFormStepBeanList = model.summaryFormStepBeanList]
    	[#list summaryFormStepBeanList as summaryFormStepBean]
	    	[#assign parametersMap = summaryFormStepBean.parameters]
	        [#assign parametersKeys = parametersMap?keys]
	        [#if parametersKeys?has_content]
		        <table cellspacing="1" cellpadding="1" border="0" width="100%" >
		        	<caption>${summaryFormStepBean.title!summaryFormStepBean.name!}</caption>
		        	<thead>
			        	<th>${i18n['formSummary.header.name']}</th>
			        	<th>${i18n['formSummary.header.value']}</th>
		        	</thead>
		        	<tbody>
			        [#list parametersKeys as parameterKey]
			        	<tr>
			        		<td>${parameterKey}</td>
			        		<td>${parametersMap[parameterKey]!}</td>
			        	</tr>
			        
			        [/#list]
			        </tbody>
		        </table>
		    [#else]
		    	<p>${i18n['summary.no.content']}</p>
	        [/#if]
        [/#list]
    </div>
    <div class="form-wrapper" >
        <form id="${content.formName?default("form0")}" method="post" action="" enctype="${def.parameters.formEnctype?default("multipart/form-data")}" >
            <div class="form-item-hidden">
				<input type="hidden" name="mgnlModelExecutionUUID" value="${content.@uuid}" />
				[#if model.formState?has_content]
					<input type="hidden" name="mgnlFormToken" value="${model.formState.token}" />
				[/#if]
            </div>
            [#if content.fieldsets?exists]
                [@cms.contentNodeIterator contentNodeCollectionName="fieldsets"]
                    [@cms.includeTemplate/]
                [/@cms.contentNodeIterator]
            [/#if]
            [#if mgnl.editMode]
                <div>[@cms.newBar contentNodeCollectionName="fieldsets"  newLabel="${i18n['form.fieldset.newLabel']}" paragraph="formGroupFields" /]</div>
            [/#if]
        </form>
    </div>
[/#if]