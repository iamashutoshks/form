

[#assign backButtonText=content.backButtonText!]

[@cms.edit/]

<div class="button-wrapper" >

    [#if backButtonText?has_content]
        <input type="submit" onclick="history.go(-1);return false;" value="${backButtonText?html}" />
    [/#if]

    <input type="submit" value="${content.buttonText!i18n['form.submit.default']?html}" />

    [@cms.area name="conditionList"/]
</div><!-- end navigation-button-wrapper -->

