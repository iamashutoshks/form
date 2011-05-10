[#assign cms=JspTaglibs["cms-taglib"]]

[#assign link = mgnl.createLink("website", content.link)]
[#assign criteria = content.criteria]

[#if mgnl.editMode]
	<div>
	[@cms.editBar /]
		[#if criteria?has_content]
			[#assign criteriaList = criteria?children]
			[#list criteriaList as criteriaItem]
				<p>
			        ${criteriaItem.condition!}, ${criteriaItem.fieldName!}: ${criteriaItem.fieldValue!}
			    </p>
	        [/#list]
	    [/#if]
	    [#if link?has_content]
		    <p>
		        ${i18n['dialog.form.criteria.tabMain.link.description']}: ${link}
		    </p>
	    [/#if]
	</div>
[/#if]