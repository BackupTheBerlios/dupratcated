/*
 * This file is part of Symphonie
 * Created : 14 mars 2005 22:36:14
 */

package fr.umlv.symphonie.util.completion;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import javax.swing.text.JTextComponent;

import fr.umlv.symphonie.util.LookableCollection;

/**
 * Class that can be used to add autocomplete support to text components
 * 
 * @author PEÑA SALDARRIAGA Sébastian
 */
public final class AutoCompleteSupport {
	
	/**
	 * Sole constructor
	 */
	private AutoCompleteSupport() {
	}

	/**
	 * Adds autcomplete support to the given text component and returns it.<br>
	 * ATTENTION : Autocomplete support cannot be removed
	 * @param textCo The component that wants autocomplete support
	 * @param dictionary The dictionary of completable words
	 * @param wordDelimiter A list of tokens(regexp) that separates words between them
	 * @return The autocompletable text component
	 */
	public static JTextComponent addSupport(final JTextComponent textCo,
			final LookableCollection<String> dictionary, String wordDelimiter) {
		
		// Verify arguments
		if (wordDelimiter == null || textCo == null || dictionary == null)
			throw new IllegalArgumentException(
					"Method doesn't take null parameters");

		final Pattern delimiter = Pattern.compile(wordDelimiter);

		textCo.addKeyListener(new KeyAdapter() {
			
			boolean isTextSelected;

			public void keyPressed(KeyEvent e) {
				isTextSelected = textCo.getSelectionStart() != textCo
						.getSelectionEnd();
			}

			public void keyReleased(KeyEvent e) {
				char charPressed = e.getKeyChar();
				int charCodePressed = e.getKeyCode();

				if (charCodePressed == KeyEvent.VK_DELETE
						|| charPressed == KeyEvent.CHAR_UNDEFINED) {
					return;
				}
				if (textCo.getSelectionStart() != textCo.getSelectionEnd()) {
					textCo.setText(textCo.getText().substring(0,
							textCo.getSelectionStart()));
				}

				String userInput = textCo.getText();
				String[] words = delimiter.split(userInput);
				String lastWord = null;
				if (words.length > 0)
					lastWord = words[words.length - 1];
				String completion = dictionary.lookup(lastWord);

				if (lastWord != null && !(lastWord.equals(""))
						&& userInput.endsWith(lastWord) && completion != null) {
					textCo.setText(userInput.substring(0, userInput.length()
							- lastWord.length())
							+ completion);
					textCo.setSelectionStart(userInput.length());
					textCo.setSelectionEnd(textCo.getText().length());
					isTextSelected = true;
				} else {
					isTextSelected = false;
				}

				if (charCodePressed == KeyEvent.VK_BACK_SPACE && isTextSelected
						&& userInput.length() > 0) {
					textCo.setText(userInput.substring(0, userInput.length()));
				}
			}
		});
		return textCo;
	}
}
