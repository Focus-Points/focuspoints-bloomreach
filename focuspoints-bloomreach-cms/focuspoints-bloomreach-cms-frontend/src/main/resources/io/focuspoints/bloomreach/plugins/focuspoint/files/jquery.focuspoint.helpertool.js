/*
 * Edited by TheFactor.e for usage within Hippo CMS
 * Gets focus point coordinates from an image - adapt to suit your needs.
 */
var focuspointHelper = (function ($) {
	var initialize = function ($container) {
		// console.log('$container');
		// console.log($container);
		var $currentHippoEditor = $container.parents('.hippo-editor-body');
		var $inputField = $('.focuspoint-input-field input', $currentHippoEditor);
		// console.log('$currentHippoEditor');
		// console.log($currentHippoEditor);
		var defaultImage;
		var $dataAttrInput;
		var $cssAttrInput;
		var $focusPointContainers;
		var $focusPointImages;
		var $helperToolImage;
		//This stores focusPoint's data-attribute values
		var focusPointAttr = {
			x: 0,
			y: 0,
			w: 0,
			h: 0
		};
		if ($inputField.val() != '') {
			var values = $inputField.val().split(',');
			focusPointAttr.x = parseFloat(values[0]);
			focusPointAttr.y = parseFloat(values[1]);
		}
		console.log(focusPointAttr);
		//Initialize Helper Tool
		(function () {
			//Initialize Variables
			defaultImage = '';
			$dataAttrInput = $('.helper-tool-data-attr', $container);
			$cssAttrInput = $('.helper-tool-css3-val', $container);
			$helperToolImage = $('.helper-tool-img, .target-overlay', $container);
			//Create Grid Elements
			for (var i = 1; i < 10; i++) {
				$('.frames', $container).append('<div class="frame' + i + ' focuspoint"><img/></div>');
			}
			//Store focus point containers
			$focusPointContainers = $('.focuspoint', $container);
			$focusPointImages = $('.focuspoint img', $container);
			//Set the default source image
			setImage(defaultImage);
			// set initial focus point
			updateReticle();
		})();
		/*-----------------------------------------*/
		// function setImage(<URL>)
		// Set a new image to use in the demo, requires URI to an image
		/*-----------------------------------------*/
		function setImage(imgURL) {
			//Get the dimensions of the image by referencing an image stored in memory
			imgURL = $("img.target-overlay", $container).attr("src");
			focusPointAttr.w = this.width;
			focusPointAttr.h = this.height;
			//Set src on the thumbnail used in the GUI
			$helperToolImage.attr('src', imgURL);
			//Set src on all .focuspoint images
			$focusPointImages.attr('src', imgURL);
			//Set up initial properties of .focuspoint containers
			/*-----------------------------------------*/
			// Note ---
			// Setting these up with attr doesn't really make a difference
			// added to demo only so changes are made visually in the dom
			// for users inspecting it. Because of how FocusPoint uses .data()
			// only the .data() assignments that follow are necessary.
			/*-----------------------------------------*/
			console.log(focusPointAttr);
			$focusPointContainers.attr({
				'data-focus-x': focusPointAttr.x,
				'data-focus-y': focusPointAttr.y,
				'data-image-w': focusPointAttr.w,
				'data-image-h': focusPointAttr.h
			});
			/*-----------------------------------------*/
			// These assignments using .data() are what counts.
			/*-----------------------------------------*/
			$focusPointContainers.data('focusX', focusPointAttr.x);
			$focusPointContainers.data('focusY', focusPointAttr.y);
			$focusPointContainers.data('imageW', focusPointAttr.w);
			$focusPointContainers.data('imageH', focusPointAttr.h);
			//Run FocusPoint for the first time.
			$('.focuspoint', $container).focusPoint();
			//Update the data attributes shown to the user
			writeValues();
		}
		/*-----------------------------------------*/
		// Update the data attributes shown to the user
		/*-----------------------------------------*/
		function writeValues() {
			$inputField.val(focusPointAttr.x.toFixed(2) + "," + focusPointAttr.y.toFixed(2));
			$dataAttrInput.val('data-focus-x="' + focusPointAttr.x.toFixed(2) + '" data-focus-y="' + focusPointAttr.y.toFixed(2) + '" data-focus-w="' + focusPointAttr.w + '" data-focus-h="' + focusPointAttr.h + '"');
		}
		/*-----------------------------------------*/
		// Bind to helper image click event
		// Adjust focus on Click / provides focuspoint and CSS3 properties
		/*-----------------------------------------*/
		$helperToolImage.click(function (e) {
			var imageW = $(this).width();
			var imageH = $(this).height();
			//Calculate FocusPoint coordinates
			var offsetX = e.pageX - $(this).offset().left;
			var offsetY = e.pageY - $(this).offset().top;
			var focusX = (offsetX / imageW - .5) * 2;
			var focusY = (offsetY / imageH - .5) * -2;
			focusPointAttr.x = focusX;
			focusPointAttr.y = focusY;
			writeValues();
			updateFocusPoint();
			updateReticle();
		});
		function updateReticle() {
			var imageW = $helperToolImage.width();
			var imageH = $helperToolImage.height();
			var focusX = focusPointAttr.x;
			var focusY = focusPointAttr.y;
			//Calculate FocusPoint coordinates
			var offsetX = ((focusX / 2) + .5 ) * imageW;
			var offsetY = ((focusY / -2) + .5) * imageH;
			//Calculate CSS Percentages
			var percentageX = (offsetX / imageW) * 100;
			var percentageY = (offsetY / imageH) * 100;
			var backgroundPosition = percentageX.toFixed(0) + '% ' + percentageY.toFixed(0) + '%';
			var backgroundPositionCSS = 'background-position: ' + backgroundPosition + ';';
			$cssAttrInput.val(backgroundPositionCSS);
			//Leave a sweet target reticle at the focus point.
			$('.reticle', $container).css({
				'top': percentageY + '%',
				'left': percentageX + '%'
			});
		}
		/*-----------------------------------------*/
		/* Update Helper */
		// This function is used to update the focuspoint 
		/*-----------------------------------------*/
		function updateFocusPoint() {
			/*-----------------------------------------*/
			// See note in setImage() function regarding these attribute assignments.
			//TLDR - You don't need them for this to work.
			/*-----------------------------------------*/
			$focusPointContainers.attr({
				'data-focus-x': focusPointAttr.x,
				'data-focus-y': focusPointAttr.y
			});
			/*-----------------------------------------*/
			// These you DO need :)
			/*-----------------------------------------*/
			$focusPointContainers.data('focusX', focusPointAttr.x);
			$focusPointContainers.data('focusY', focusPointAttr.y);
			$focusPointContainers.adjustFocus();
		}
		$container.addClass('initialized');
	};
	/*
	 Explose public functions
	 */
	return {
		initialize: initialize
	}
}(jQuery));
