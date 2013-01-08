
classDef("mgnl.form.FormDialogs", {

    togleDisplay: function(id, visible) {
      var div = document.getElementById(id);
      if(visible == false) {
        div.style.display = "none";
      } else {
        div.style.display = "inline";
      }
  },

  changeSelection: function(name, options, value) {
    var newName = name;
    var locale = "";
    var idx = name.lastIndexOf("_");
    if (idx && idx > 0) {
      newName = name.substring(0, idx);
      locale = "_" + name.substring(idx + 1);
    }
    for(var i=0; i < options.length; i++) {
      if(options[i].value == value) {
        this.togleDisplay(newName + options[i].value + locale + "_radioswich_div", true);
      } else {
        this.togleDisplay(newName + options[i].value + locale + "_radioswich_div", false);
      }
    }
  },

  onSelectionChanged: function(name, value) {
    var radios = document.getElementsByName(name);
    this.changeSelection(name, radios, value);
  }

});
