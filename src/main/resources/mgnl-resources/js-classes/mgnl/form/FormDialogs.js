
classDef("mgnl.form.FormDialogs", {

    togleDisplay: function(id, visible) {
      var div = document.getElementById(id);
      if(visible == false) {
        div.style.display = "none";
      } else {
        div.style.display = "inline";
      }
  },

  changeSelection: function(name, options, value, locale) {
    if (locale == null) {
      locale = "";
    }
    for(var i=0; i < options.length; i++) {
      if(options[i].value == value) {
        this.togleDisplay(name + options[i].value + locale + "_radioswich_div", true);
      } else {
        this.togleDisplay(name + options[i].value + locale + "_radioswich_div", false);
      }
    }
  },

  onSelectionChanged: function(name, value, locale) {
    var radios = document.getElementsByName(name);
    this.changeSelection(name, radios, value, locale);
  }

});
