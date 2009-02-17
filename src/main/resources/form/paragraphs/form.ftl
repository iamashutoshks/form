[#assign cms=JspTaglibs["cms-taglib"]]

[#if actionResult == "success"]
    <div class="text success">
        <h1>${content.successTitle!i18n['form.default.successTitle']}</h1>
        <p>${content.successMessage!}</p>
    </div><!-- end text -->
[#else]
    [#if model.errorMessages?size > 0]
        <div class="text error">
            <h1>${content.errorTitle!i18n['form.default.errorTitle']}</h1>
            <ul>
                [#assign keys = model.errorMessages?keys]
                [#list keys as key]
                    <li>
                        <a href="#${key}_label">${model.errorMessages[key]}</a>
                    </li>
                [/#list]
            </ul>
        </div> <!-- end error message -->
    [/#if]
    [@cms.editBar editLabel="${i18n['form.editLabel']}" moveLabel="" deleteLabel="" /]
    <div class="text">
        <h1>${content.formTitle!}</h1>
        <p>${content.formText!}</p>
    </div><!-- end text -->
    <div class="form-wrapper" >
        <form id="${content.formName?default("form0")}" method="post" action="" enctype="multipart/form-data" >

                <input type="text" style="display:none;" name="field" value="" />
                [#if content.fieldsets?exists]
                    [@cms.contentNodeIterator contentNodeCollectionName="fieldsets"]
                        [@cms.includeTemplate/]
                    [/@cms.contentNodeIterator]
                [/#if]
                [#if mgnl.editMode]
                    <div>[@cms.newBar contentNodeCollectionName="fieldsets"  newLabel="${i18n['form.fieldset.newLabel']}" paragraph="formGroupFields" /]</div>
                [/#if]
        </form>
    </div> <!-- end form -->
[/#if] <!-- end else -->

