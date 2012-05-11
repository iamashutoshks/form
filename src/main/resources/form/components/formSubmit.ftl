
[#assign backButtonText=content.backButtonText!]

<div class="button-wrapper" >

    [#if backButtonText?has_content]
      <script type="text/javascript">
        function mgnlFormHandleBackButton(el) {
          var back = document.createElement('input')
          back.setAttribute('type','hidden')
          back.setAttribute('name', 'mgnlFormBack')
          //get the enclosing form. This is widely supported, even by IE4!
          el.form.appendChild(back)
          el.form.submit()
        }
      </script>
        <input type="submit" onclick="return mgnlFormHandleBackButton(this)" value="${backButtonText?html}" />
    [/#if]

    <input type="submit" value="${content.buttonText!i18n['form.submit.default']?html}" />

    [@cms.area name="conditionList"/]
</div><!-- end navigation-button-wrapper -->

