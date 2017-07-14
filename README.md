# FlowerAlmanacFirebase
(Version implementing data storage in Google Firebase. Extension of final project for UP ITDC.)

A gardening tool app allowing users to add, view, edit and delete information on planting flowers.
Information include a rating on the ease of planting and helpful tips on how to grow a specific flower.
Users may also choose to attach a photo of the flower taken by camera, or via gallery submission.

<div>
<h4>Login page using Firebase UI</h4>
<img src="https://user-images.githubusercontent.com/29102307/28212272-3e0d7b96-68d3-11e7-98c8-4827f7e78a65.png" width="270">
</div>

<div>
<h4>App Main Page</h4>
<img src="https://user-images.githubusercontent.com/29102307/28212305-5f78830c-68d3-11e7-80fc-9b8e980b3e50.png" width="250">
<p>Successful login takes users to the main page, where they can perform add, view, edit and delete operations.</p>
<p>Clicking on the purple button allows users to add new flowers to the list.</p>
</div>

<div>
<h4>Add and Edit Page</h4>
<img src="https://user-images.githubusercontent.com/29102307/28212304-5f48d76a-68d3-11e7-8fa7-6320d39a38e3.png" width="250">
<p>Users may not add multiple flowers with the same name. As soon as an existing flower name is keyed in, an <b>error message</b> is displayed and the save button is <b>disabled</b>.</p>
</div>

<div>
<h4 style="float:right; display:inline-block;">Add and Edit Page</h4>
<img src="https://user-images.githubusercontent.com/29102307/28212307-601b1ebe-68d3-11e7-803f-ac2c406e1087.png" width="250">
<p>Photos may be added via camera or gallery submission.</p>
</div>

<div>
<h4>Add and Edit Page</h4>
<img src="https://user-images.githubusercontent.com/29102307/28212299-5f2c153a-68d3-11e7-99b0-1f8cc9a40b1e.png" width="250">
<p>Users have the option to replace or remove photos after attaching them, by clicking on the white clear button. Photos may be removed even after saving the item, as part of the edit feature. If a photo is removed in the edit page, saving changes will prompt the app to <b>delete the photo from the Firebase Storage</b> as well.</p>
<img src="https://user-images.githubusercontent.com/29102307/28212306-5fb119d8-68d3-11e7-8348-6755dfd96983.png" width="250">
<p>Adding, editing, or deleting flowers in the list will refresh the main page to reflect these changes.</p>
</div>

<div>
<h4>Accessing Edit and Delete Features</h4>
<p>From the main page, long press on items will allow users access to edit and delete features.</p>
<p>Clicking on a flower in the list takes users to its view page. Users may also perform edit and delete actions once in the view page.</p>
<img src="https://user-images.githubusercontent.com/29102307/28212300-5f389c9c-68d3-11e7-930c-022cd8aabd70.png" width="250">
<img src="https://user-images.githubusercontent.com/29102307/28212303-5f445e38-68d3-11e7-9e82-280c44a339a2.png" width="250">
</div>

<div>
<h4>Deleting</h4>
<p>Any time the delete button is selected, the user will be asked to confirm the action before proceeding.</p>
<img src="https://user-images.githubusercontent.com/29102307/28212301-5f4030f6-68d3-11e7-8cc0-0111d3002d88.png" width="250">
<img src="https://user-images.githubusercontent.com/29102307/28212302-5f41f2ba-68d3-11e7-973c-8324d5ff4543.png" width="250">
</div>

<div>
<h4>Signing Out</h4>
<p>Users may sign out of the application from the main page, by clicking on the power button. This is located in the action toolbar, at the upper right corner of the screen. A sign out confirmation is asked before proceeidng.</p>
<img src="https://user-images.githubusercontent.com/29102307/28214402-b1f5656a-68dc-11e7-9ca9-25febc7fbffa.png" width="250">
</div>
