[#assign cms=JspTaglibs["cms-taglib"]]

<div>
[@cms.editBar /]
    [#if content.title?has_content]
        <h4>${content.title}</h4>
    [/#if]
    [#if content.legend?has_content]
        <fieldset ${content.horizontal?string("class=\"mod\"", "")} >
            <legend>${content.legend}</legend>
    [/#if]

    [#if content.type?index_of("select") < 0]

        [#list content.labels?split("\r\n") as label]
            [#assign checked=""]
            [#assign data=label?split(":")]
            [#if model.value == data[1] ]
                [#assign checked="checked=\"checked\""]
            [/#if]

            <label id="${content.controlName}_label" for="${content.controlName}_${label_index}">
                <input id="${content.controlName}_${label_index}" name="${content.controlName}" type="${content.type}" value="${data[1]}" ${checked} />
                    ${data[0]}
            </label>
      [/#list]
    [#else]
        <select id="${content.controlName}" name="${content.controlName}" ${content.multiple?string("multiple=\"multiple\"", "")}>
            [#list content.labels?split("\r\n") as label]
                [#assign selected=""]
                [#assign data=label?split(":")]
                [#if model.value == data[1] ]
                    [#assign selected="selected=\"selected\""]
                [/#if]
                <option value="${data[1]}" ${selected} >${data[0]}</option>
            [/#list]
        </select>
    [/#if]

    [#if content.legend?has_content]
        </fieldset>
    [/#if]
</div>
