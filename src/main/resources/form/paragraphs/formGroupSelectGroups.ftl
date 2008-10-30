[#assign cms=JspTaglibs["cms-taglib"]]

[#assign parent = content?parent]
<optgroup id="${parent?parent.controlName}_${content.controlName}" label="${content.title}"  >

    [#assign values=content.values?split("\r\n")]
	[#list content.labels?split("\r\n") as label]
        [#assign selected=""]
		  [#if model.value == values[label_index]]
		    [#assign selected="selected"]
		  [/#if]
		<option value="${values[label_index]}" ${selected} >${label}</option>
    [/#list]

</optgroup>



