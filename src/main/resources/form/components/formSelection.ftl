

[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Include: Global --]
[#include "/form/components/init.required.ftl"]


[#-------------- RENDERING PART --------------]

<div ${model.style!}>
    [#if content.title?has_content]
        <label for="${content.controlName!''}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${content.title!}
            [#if content.mandatory!false]
                <dfn title="required">${model.requiredSymbol!}</dfn>
            [/#if]
            </span>
        </label>
    [/#if]

    <fieldset ${content.horizontal?string("class=\"mod\"", "")} >
        [#if content.legend?has_content]
            <legend>${content.legend}</legend>
        [/#if]

        [#if content.type?index_of("select") < 0 && content.labels?has_content]
            [#assign formItems=cmsfn.decode(content).labels?split("(\r\n|\r|\n|\x0085|\x2028|\x2029)", "rm")]
            [#list formItems as label]
                [#assign checked=""]
                [#assign data=label?split(":")]

                [#if model.value == data[1]!data[0] ]
                    [#assign checked="checked=\"checked\""]
                [/#if]
                [#if formItems?size > 1 && content.type = "checkbox"]
                    <div class="form-item">
                        <input type="${content.type}" id="${(content.controlName!'')}_${label_index}" name="${(content.controlName!'')}" value="${(data[1]!data[0])!?html}" ${checked!} />
                        <label for="${(content.controlName!'')}_${label_index}">${data[0]!?html}</label>
                    </div><!-- end form-item -->
                [#else]
                    <div class="form-item">
                        <input ${requiredAttribute!} type="${content.type}" id="${(content.controlName!'')}_${label_index}" name="${(content.controlName!'')}" value="${(data[1]!data[0])!?html}" ${checked!} />
                        <label for="${(content.controlName!'')}_${label_index}">${data[0]!?html}</label>
                    </div><!-- end form-item -->
                [/#if]
            [/#list]
            <div id="checkbox-error" class="text error" style="display:none">
                <ul>
                    <li>${i18n['form.user.errorMessage.checkboxes']}</li>
                </ul>
            </div>
            [#if requiredAttribute?has_content && content.type="checkbox" && content.controlName?has_content && formItems?size > 1]
                <script type="text/javascript">
                    var checkboxes = document.getElementsByName("${content.controlName}");
                    var element = checkboxes[0];
                    while (element.nodeName != "FORM") {
                        element = element.parentNode;
                    }
                    var valid = false;
                    element.onsubmit = function () {
                        for (var i = 0; i < checkboxes.length; i++) {
                            if (checkboxes[i].checked) {
                                valid = true;
                                break;
                            }
                        }
                        if (valid) {
                            document.getElementById("checkbox-error").style.display = "none";
                        } else {
                            document.getElementById("checkbox-error").style.display = "block";
                        }
                        return valid;
                    }
                </script>
            [/#if]
        [#else]
            <select ${requiredAttribute!} id="${(content.controlName!'')}" name="${(content.controlName!'')}" ${content.multiple?string("multiple=\"multiple\"", "")}>
                [#if content.labels?has_content]
                    [#list cmsfn.decode(content).labels?split("(\r\n|\r|\n|\x0085|\x2028|\x2029)", "rm") as label]
                                [#assign selected=""]
                        [#assign data=label?split(":")]
                        [#if model.value == data[1]!data[0] ]
                            [#assign selected="selected=\"selected\""]
                        [/#if]
                        <option value="${(data[1]!data[0])!?html}" ${selected!} >${data[0]!?html}</option>
                    [/#list]
                [/#if]
            </select>
        [/#if]
    </fieldset>

</div><!-- end ${model.style!} -->