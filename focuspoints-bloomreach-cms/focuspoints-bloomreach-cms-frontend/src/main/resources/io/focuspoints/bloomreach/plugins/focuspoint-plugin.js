(function ($) {
	var initFocuspointButton = function (clicked) {

		$('.focuspoint-button').each(function () {

			$(this).removeAttr('onclick');

			$(this).on('click', function () {
				// console.log('click');
				var $this = $(this);
				var $currentItem = $this.parents('.hippo-editor');
				var $focuspointContainer = $('.focuspoint-container', $currentItem);

				$focuspointContainer.toggle();
				if (!$focuspointContainer.hasClass('initialized')) {
					focuspointHelper.initialize($focuspointContainer);
				}

			});

			if (clicked) {
				$(this).click();
			}

		});

	};

	var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;

	$.fn.mutated = function (callback) {
		if (MutationObserver) {
			var options = {
				childList: true
			};

			var observer = new MutationObserver(function (mutations) {
				mutations.forEach(function (e) {

					if (e.addedNodes.length > 0) {
						callback.call(e.target, e.attributeName);
					}
				});
			});

			return this.each(function () {
				observer.observe(this, options);
			});

		}
	};

	var listen = function (name) {
		$(name + ' *').mutated(function (attrName) {

			var find = 'browse-perspective-center';

			if ($(this).hasClass(find)) {

				listen('.' + find);
			}

			if ($(this).parents('.' + find).length > 0) {
				initFocuspointButton();
			}


		});
	};

	$(document).ready(function () {
		$(document).on('focus-button-clicked', function () {
			initFocuspointButton(true);
		});
		//listen('body');
		initFocuspointButton(false);
	});

})(jQuery);
