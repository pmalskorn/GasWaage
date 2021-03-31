/*
 * GasWaage.c
 *
 * Author : Philipp Malskorn
 */ 

#include <avr/io.h>
#include <util/delay.h>
#include <stdint.h>
#include <avr/sleep.h>
#include <avr/interrupt.h> 

#include "dbg_putchar.h"

#define DATA_OUT PINB4
#define PD_SCK PINB3

uint32_t read_weight(){
	
	// set clock to LOW
	PORTB &= ~(1 << PD_SCK);
	// wait until the HX711 is ready
	while ((PINB >> DATA_OUT) & 1) _delay_ms(1);
	
	volatile uint32_t data = 0;
	
	// we need to read 24 bits
	// one bit after every positive edge  
	for (int i = 0; i < 24; i++)
	{
		// positive pulse
		PORTB |= (1 << PD_SCK);
		PORTB &= ~(1 << PD_SCK);
		
		// read bit
		data = data << 1;
		data |= (PINB >> DATA_OUT) & 0x01;
	}
	
	// set clock back to LOW
	PORTB |= (1 << PD_SCK);
	
	return data;
}

ISR(INT0_vect){
	cli();
	
	// READ WEIGHT
	uint32_t data = read_weight();
	
	// SEND DATA as ASCII Char
	// could be reworked to reduce data
	char string[8]  = { 0 };
	for(int i = 0; i < 8; i++){
		string[i] = data % 10 + '0';
		data = data / 10;
	}
	for(int i = 7; i >= 0; i--){
		dbg_putchar(string[i]);
	}
	dbg_putchar('\n');
	
	// delete buffered interrupt calls
	GIFR |= (1 << INT0);
	sei();
}


int main(void)
{
	// shutdown adc
	PRR |= (1 << PRADC);
	
	// select power down option for sleep mode
	MCUCR &= ~(1 << SM0);
	MCUCR |= (1 << SM1);
	
	// select the low Level interrupt for INT0 (PINB1)
	MCUCR &= ~((1 << ISC01) | (1 << ISC00));
	
	// enable external interrupts INTO (PINB1)
	// is connect to the transmit (tx) of the HM-10
	GIMSK |=  (1 << INT0);
		
	// config DATA_OUT Pin as input 
	// is connected to data_out of the HX711
	DDRB &= ~(1 << DATA_OUT);
	
	// config PD_SCK Pin as output
	// is connected to SCK of the HX711
	DDRB |= (1 << PD_SCK);
	// set clock to normal position (HIGH)
	PORTB |= (1 << PD_SCK);
	
	// globally enable interrupts
	sei();

	// initialize the software UART only transmit (tx)
	dbg_tx_init();

    while (1) 
    {
		// send up message
		dbg_putchar('U');
		dbg_putchar('P');
		dbg_putchar('!');
		dbg_putchar('\n');
		
		// set mcu to sleep
		// wake up on interrupt INT0
		sleep_mode();
    }	
	
}

