export interface ApplicationItem {
  name: string;
  required: boolean;
  description: Map<string, string>;
  displayName: Map<string, string>;
  type: string;
  allowedValues: string[];
}
