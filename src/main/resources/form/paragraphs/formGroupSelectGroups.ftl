[#assign cms=JspTaglibs["cms-taglib"]]
[#if mgnl.editMode]
    <div style="float:right;height:20px;width:100px">[@cms.editBar /]</div>
[/#if]
[#assign parent = content?parent]
[#assign parentmodel = model.parentModel]
[#if !mgnl.editMode]
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
[#else]
<p>
    ${content.title} : ${content.labels}
</p>
[/#if]


