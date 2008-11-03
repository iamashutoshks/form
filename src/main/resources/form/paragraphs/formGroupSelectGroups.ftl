[#assign cms=JspTaglibs["cms-taglib"]]
[@cms.editBar /]
[#assign parent = content?parent]
[#assign parentmodel = model.parentModel]
<optgroup id="${parent?parent.controlName}_${content.controlName}" label="${content.title}"  >

    [#assign values=content.values?split("\r\n")]
    [#list content.labels?split("\r\n") as label]
        [#assign selected=""]
        [#list parentmodel.value?split("*") as modelValue]
            [#if modelValue == values[label_index]]
                [#assign selected="selected"]
                [#break]
            [/#if]
        [/#list]
        <option value="${values[label_index]}" ${selected} >${label}</option>

    [/#list]
</optgroup>



