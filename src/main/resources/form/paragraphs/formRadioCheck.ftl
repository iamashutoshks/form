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
    [#assign values=content.values?split("\r\n")]
    [#list content.labels?split("\r\n") as label]
        [#assign checked=""]
        [#if model.value == values[label_index]]
            [#assign checked="checked=\"checked\""]
        [/#if]
        <label id="${content.controlName}_label" for="${content.controlName}_${label_index}">
            <input id="${content.controlName}_${label_index}" name="${content.controlName}" type="${content.type}" value="${values[label_index]}" ${checked} />
                ${label}
        </label>
  [/#list]

    [#if content.legend?has_content]
        </fieldset>
    [/#if]
</div>
