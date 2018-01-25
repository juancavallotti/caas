[![Build Status](https://travis-ci.org/advanced-rest-client/raml-request-url-editor.svg?branch=stage)](https://travis-ci.org/advanced-rest-client/raml-request-url-editor)  

# raml-request-url-editor

`<raml-request-url-editor>` An URL editor for the RAML request panel

Use this input element to provide URI variables support.
If the `uriParameters` array is set, and the user click on the variable name
then the documentation for this variable will be displayed.
This behavior can be turned off by setting `skip-docs` attribute (or `skipDocs`
property).

The element has custom validation. If the value contains a variable string
("{some string}") then it will display a validation error.

### Example
```html
<raml-request-url-editor auto-validate required></raml-request-url-editor>
```
```javascript
var input = document.querySelector('raml-request-url-editor');
input.addEventListener('value-changed', function(e) {
  if (input.validate())
    var url = e.detail.value;
});
```

When the `value` of this control change then the `url-value-changed` event will
be fired. Also, if other element or the application will send this event then
the value of this form control will be updated. This is a way to update URL
value without directly accessing the element.

```jsvascript
document.dispatchEvent(new CustomEvent('url-value-changed', {
  bubbles: true,
  detail: {
    value: 'http://www.domain.com'
  }
}));
```

Firing this event will update the value to `http://www.domain.com`.

### Styling
`<raml-request-url-editor>` provides the following custom properties and mixins for styling:

Custom property | Description | Default
----------------|-------------|----------
`--raml-request-url-editor` | Mixin applied to the element | `{}`
`--raml-request-url-editor-documentation` | Mixin applied to the documentation field. Not that this node has the `--arc-font-body1` mixin and also `markdown-styles` applies. | `{}`

Additionally use styles defined for the `paper-input` element.



### Events
| Name | Description | Params |
| --- | --- | --- |
| url-value-changed | Fired when the URL value change. Note that this event is fifed before validation occur and therefore the URL may be invalid. | value **String** - The URL. |
