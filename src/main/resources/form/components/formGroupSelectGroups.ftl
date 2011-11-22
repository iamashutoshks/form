[@cms.edit /]

[#assign parent = content?parent]

[#if !cmsfn.editMode]
<optgroup id="${parent?parent.controlName}_${content.controlName}" label="${content.title}"  >
[#else]
<p>${content.title!}</p>
<select multiple="true" >
[/#if]
    [#list content.labels?split("\r\n") as label]
        [#assign selected=""]
        [#assign data=label?split(":")]
        [#list model.parent.value?split("*") as modelValue]
            [#if modelValue == data[1]!data[0]]
                [#assign selected="selected=\"selected\""]
                [#break]
            [/#if]
        [/#list]
        <option value="${data[1]!data[0]!?html}" ${selected} >${data[0]}</option>
    [/#list]
[#if !mgnl.editMode]
</optgroup>
[#else]
</select>
[/#if]


