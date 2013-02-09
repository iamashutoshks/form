[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!}>
    [@cms.editBar /]

    [#if content.title?has_content]
        <label for="${content.controlName!''}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${mgnl.encode(content).title!}
            [#if content.mandatory!false]
                <dfn title="required">${model.requiredSymbol!?html}</dfn>
            [/#if]
            </span>
        </label>
    [/#if]

    <fieldset ${content.horizontal?string("class=\"mod\"", "")} >
		[#if content.legend?has_content]
		        <legend>${content.legend}</legend>
		[/#if]

		[#if content.type?index_of("select") < 0 && content.labels?has_content]
		    [#list content.labels?split("\r\n") as label]
		        [#assign checked=""]
		        [#assign data=label?split(":")]

		        [#if model.value == data[1]!data[0] ]
		            [#assign checked="checked=\"checked\""]
		        [/#if]
		        <div class="form-item">
					<input type="${content.type}" id="${content.controlName!''}_${label_index}" name="${content.controlName!''}" value="${data[1]!data[0]!?html}" ${checked} />
			        <label for="${content.controlName!''}_${label_index}">${data[0]}</label>
		        </div>
		    [/#list]
		[#else]
		    <select id="${content.controlName!''}" name="${content.controlName!''}" ${content.multiple?string("multiple=\"multiple\"", "")}>
		        [#if content.labels?has_content]
		            [#list content.labels?split("\r\n") as label]
                            [#assign selected=""]
		                [#assign data=label?split(":")]
		                [#if model.value == data[1]!data[0] ]
		                    [#assign selected="selected=\"selected\""]
		                [/#if]
		                <option value="${data[1]!data[0]!?html}" ${selected} >${data[0]}</option>
		            [/#list]
		        [/#if]
		    </select>
		[/#if]

    </fieldset>

</div>
