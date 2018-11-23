import {Attribute} from "../../core/models/Attribute";

export interface RequestItem {

  getAttribute(): Attribute;

  hasCorrectValue(): boolean;

  onFormSubmitted(): void;
}
