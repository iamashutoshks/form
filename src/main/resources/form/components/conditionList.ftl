

[#if cmsfn.editMode]
    <div class="condition-list">
        <br/>

        [#-- FIXME the former label was 'newLabel="${i18n['condition.newLabel']}"' --]
        [@cms.edit/]

        [#list components as component ]
            [@cms.render content=component /]
        [/#list]

    </div><!-- end condition-list -->
[/#if]