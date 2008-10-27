[#assign cms=JspTaglibs["cms-taglib"]]


<optgroup id="${content.controlName}" label="${content.title}"  >

    [#assign values=content.values?split("\r\n")]
	[#list content.labels?split("\r\n") as label]

		<option value="${values[label_index]}" >${label}</option>
    [/#list]

</optgroup>



